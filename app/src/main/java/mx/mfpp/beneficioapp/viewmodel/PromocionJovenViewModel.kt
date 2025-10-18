package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.PromocionJoven
import mx.mfpp.beneficioapp.model.ServicioRemotoJovenesPromocion

class PromocionJovenViewModel: ViewModel() {
    // Solo las secciones que necesitas
    private val _nuevasPromociones = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val nuevasPromociones: StateFlow<List<PromocionJoven>> = _nuevasPromociones.asStateFlow()

    private val _promocionesExpiracion = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val promocionesExpiracion: StateFlow<List<PromocionJoven>> = _promocionesExpiracion.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarPromocionesDelBackend()
    }

    fun cargarPromocionesDelBackend() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Obtener promociones reales del backend
                val promocionesReales = ServicioRemotoJovenesPromocion.obtenerPromociones()

                // Solo asignar las secciones que necesitas
                _nuevasPromociones.value = promocionesReales.take(4) // Primeras 4 como nuevas
                _promocionesExpiracion.value = promocionesReales.filter { esPromocionPorExpiracion(it) }

            } catch (e: Exception) {
                _error.value = "Error al cargar promociones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun esPromocionPorExpiracion(promocion: PromocionJoven): Boolean {
        // Lógica simple para determinar si expira pronto
        return promocion.id % 2 == 0 // Ejemplo: promociones pares "expiran pronto"
    }

    fun refrescarPromociones() {
        cargarPromocionesDelBackend()
    }

    fun clearError() {
        _error.value = null
    }
}