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

class NegocioDetalleViewModel(
    private val establecimientoId: Int
) : ViewModel() {

    private val _promociones = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val promociones: StateFlow<List<PromocionJoven>> = _promociones.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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