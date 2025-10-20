// mx.mfpp.beneficioapp.viewmodel.HistorialViewModel.kt
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
 * ViewModel para manejar el historial de promociones usadas
 */
class HistorialViewModel : ViewModel() {

    private val _historial = MutableStateFlow<List<HistorialPromocionUsuario>>(emptyList())
    val historial: StateFlow<List<HistorialPromocionUsuario>> = _historial.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Carga el historial de promociones usadas por un usuario
     */
    fun cargarHistorialUsuario(idUsuario: Int) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val historialData = ServicioRemotoHistorial.obtenerHistorialUsuario(idUsuario)
                _historial.value = historialData

                if (historialData.isEmpty()) {
                    _error.value = "No hay historial de promociones usadas"
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar historial: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga el historial de promociones de un establecimiento (opcional)
     */
    fun cargarHistorialEstablecimiento(idEstablecimiento: Int) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val historialData = ServicioRemotoHistorial.obtenerHistorialEstablecimiento(idEstablecimiento)
                _historial.value = historialData

                if (historialData.isEmpty()) {
                    _error.value = "No hay historial de promociones para este establecimiento"
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar historial: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresca el historial
     */
    fun refrescarHistorial(idUsuario: Int) {
        cargarHistorialUsuario(idUsuario)
    }

    fun clearError() {
        _error.value = null
    }
}