package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.HistorialPromocionUsuario
import mx.mfpp.beneficioapp.model.ServicioRemotoHistorial

/**
 * ViewModel para manejar el historial de promociones usadas por un usuario.
 *
 * Gestiona la carga de datos desde el servidor, control de estado de carga
 * y manejo de errores.
 */
class HistorialViewModel : ViewModel() {

    /** Lista del historial de promociones usadas por el usuario */
    private val _historial = MutableStateFlow<List<HistorialPromocionUsuario>>(emptyList())
    val historial: StateFlow<List<HistorialPromocionUsuario>> = _historial.asStateFlow()

    /** Indica si se está realizando alguna operación de carga */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Mensaje de error en caso de fallo al cargar el historial */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Carga el historial de promociones usadas por un usuario desde el servidor.
     *
     * @param idUsuario ID del usuario cuyo historial se desea cargar.
     */
    fun cargarHistorialUsuario(idUsuario: Int) {
        if (idUsuario == 0) {
            _error.value = "ID de usuario no válido"
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val historialData = ServicioRemotoHistorial.obtenerHistorialUsuario(idUsuario)
                _historial.value = historialData

                if (historialData.isEmpty()) {
                    _error.value = "No tienes promociones usadas aún"
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar historial: ${e.message ?: "Verifica tu conexión"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresca el historial del usuario recargando los datos desde el servidor.
     *
     * @param idUsuario ID del usuario cuyo historial se desea refrescar.
     */
    fun refrescarHistorial(idUsuario: Int) {
        cargarHistorialUsuario(idUsuario)
    }

    /**
     * Limpia cualquier mensaje de error actual.
     */
    fun clearError() {
        _error.value = null
    }
}