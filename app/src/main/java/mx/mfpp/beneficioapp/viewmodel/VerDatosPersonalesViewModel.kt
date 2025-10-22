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

class VerDatosPersonalesViewModel : ViewModel() {

    private val _joven = MutableStateFlow<Joven?>(null)
    val joven: StateFlow<Joven?> = _joven

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarDatos(context: Context) {
        viewModelScope.launch {
            try {
                val session = SessionManager(context)
                val idUsuario = session.getJovenId()


                if (idUsuario == null) {
                    _error.value = "No se encontró ID de usuario en sesión."
                    _cargando.value = false
                    return@launch
                }

                val datos = ServicioRemotoObtenerDatosJoven.obtenerDatosJoven(idUsuario)
                if (datos != null) {
                    _joven.value = datos
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
