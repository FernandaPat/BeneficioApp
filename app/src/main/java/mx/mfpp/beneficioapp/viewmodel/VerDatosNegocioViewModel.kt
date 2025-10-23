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

class VerDatosNegocioViewModel : ViewModel() {

    private val _negocio = MutableStateFlow<Negocio?>(null)
    val negocio: StateFlow<Negocio?> = _negocio

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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

                    // ✅ Guarda la foto en sesión para mostrarla en el InicioNegocioPage
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
