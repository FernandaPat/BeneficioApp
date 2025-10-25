package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Joven
import mx.mfpp.beneficioapp.model.ServicioRemotoObtenerDatosJoven
import mx.mfpp.beneficioapp.model.SessionManager
/**
 * ViewModel encargado de obtener y exponer los datos personales del usuario (joven).
 *
 * Gestiona la carga de información desde el servidor y mantiene
 * el estado de carga y errores para la UI.
 */
class VerDatosPersonalesViewModel : ViewModel() {

    /** Datos del joven cargados desde el servidor */
    private val _joven = MutableStateFlow<Joven?>(null)
    val joven: StateFlow<Joven?> = _joven

    /** Indica si actualmente se están cargando los datos */
    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    /** Mensaje de error en caso de fallas al cargar los datos */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Carga los datos personales del joven desde el servidor.
     *
     * @param context Contexto de la aplicación, necesario para acceder
     *                a la sesión y obtener el ID del usuario.
     *
     * - Si no se encuentra el ID del usuario en la sesión, se actualiza [_error].
     * - Guarda la foto de perfil en la sesión para futuras referencias.
     */
    fun cargarDatos(context: Context) {
        viewModelScope.launch {
            try {
                val session = SessionManager(context)
                val idUsuario = session.getJovenId()

                if (idUsuario == null) {
                    _error.value = "No se encontró ID del usuario en sesión."
                    _cargando.value = false
                    return@launch
                }

                val datos = ServicioRemotoObtenerDatosJoven.obtenerDatosJoven(idUsuario)
                if (datos != null) {
                    _joven.value = datos
                    // Guarda la URL de la foto directamente en la sesión
                    session.setFotoPerfil(datos.foto)
                } else {
                    _error.value = "No se pudieron cargar los datos del joven."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }
}