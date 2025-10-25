// mx.mfpp.beneficioapp.viewmodel.NegocioDetalleViewModel
package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.PromocionJoven
import mx.mfpp.beneficioapp.model.ServicioRemotoNegocioDetalle

/**
 * ViewModel para manejar los detalles de un negocio y sus promociones activas.
 *
 * @param establecimientoId ID del establecimiento cuyo detalle se desea cargar
 */
class NegocioDetalleViewModel(
    private val establecimientoId: Int
) : ViewModel() {

    /** Lista de promociones activas del negocio */
    private val _promociones = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val promociones: StateFlow<List<PromocionJoven>> = _promociones.asStateFlow()

    /** Indica si se está cargando información */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Mensaje de error actual */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Carga las promociones activas del establecimiento desde el servicio remoto.
     *
     * Actualiza los estados de carga y error automáticamente.
     */
    fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val promos = ServicioRemotoNegocioDetalle.obtenerPromocionesActivas(establecimientoId)
                _promociones.value = promos
            } catch (e: Exception) {
                _error.value = "Error cargando promociones: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Factory para crear instancias de [NegocioDetalleViewModel] con parámetros.
     *
     * @param establecimientoId ID del establecimiento a pasar al ViewModel
     */
    class NegocioDetalleViewModelFactory(
        private val establecimientoId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NegocioDetalleViewModel::class.java)) {
                return NegocioDetalleViewModel(establecimientoId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}