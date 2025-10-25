package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Negocio
import mx.mfpp.beneficioapp.model.ServicioRemotoObtenerDatosNegocio
import mx.mfpp.beneficioapp.model.SessionManager
/**
 * ViewModel encargado de obtener y exponer los datos de un negocio.
 *
 * Gestiona la carga de información desde el servidor y mantiene
 * el estado de carga y errores para la UI.
 */
class VerDatosNegocioViewModel : ViewModel() {

    /** Datos del negocio cargados desde el servidor */
    private val _negocio = MutableStateFlow<Negocio?>(null)
    val negocio: StateFlow<Negocio?> = _negocio

    /** Indica si actualmente se están cargando los datos */
    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    /** Mensaje de error en caso de fallas al cargar los datos */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Carga los datos del negocio desde el servidor.
     *
     * @param context Contexto de la aplicación, necesario para acceder
     *                a la sesión y obtener el ID del negocio.
     *
     * - Si no se encuentra el ID del negocio en la sesión, se actualiza [_error].
     * - Guarda la foto de perfil en la sesión para futuras referencias.
     */
    fun cargarDatos(context: Context) {
        viewModelScope.launch {
            try {
                val session = SessionManager(context)
                val idNegocio = session.getNegocioId()

                if (idNegocio == null) {
                    _error.value = "No se encontró ID del negocio en sesión."
                    _cargando.value = false
                    return@launch
                }

                val datos = ServicioRemotoObtenerDatosNegocio.obtenerDatosNegocio(idNegocio)
                if (datos != null) {
                    _negocio.value = datos
                    // Guarda la foto en sesión para mostrarla en otras pantallas
                    session.setFotoPerfil(datos.foto)
                } else {
                    _error.value = "No se pudieron cargar los datos del negocio."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }
}