package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.PromocionJoven
import mx.mfpp.beneficioapp.model.ServicioRemotoJovenesPromocion
import java.text.SimpleDateFormat
import java.util.*

class PromocionJovenViewModel: ViewModel() {
    private val _nuevasPromociones = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val nuevasPromociones: StateFlow<List<PromocionJoven>> = _nuevasPromociones.asStateFlow()

    private val _promocionesExpiracion = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val promocionesExpiracion: StateFlow<List<PromocionJoven>> = _promocionesExpiracion.asStateFlow()

    // NUEVO: StateFlow para todas las promociones
    private val _todasPromociones = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val todasPromociones: StateFlow<List<PromocionJoven>> = _todasPromociones.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Formato de fecha para parsear las fechas del API
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        cargarPromocionesDelBackend()
    }

    fun cargarPromocionesDelBackend() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val promocionesReales = ServicioRemotoJovenesPromocion.obtenerPromociones()

                // Filtrar promociones activas
                val promocionesActivas = promocionesReales.filter { it.estado == "activa" }

                // Aplicar lógica de filtrado
                _nuevasPromociones.value = filtrarNuevasPromociones(promocionesActivas)
                _promocionesExpiracion.value = filtrarPromocionesPorExpiracion(promocionesActivas)

                // NUEVO: Guardar todas las promociones activas
                _todasPromociones.value = promocionesActivas

            } catch (e: Exception) {
                _error.value = "Error al cargar promociones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun filtrarNuevasPromociones(promociones: List<PromocionJoven>): List<PromocionJoven> {
        return promociones.filter { promocion ->
            esPromocionReciente(promocion.fecha_creacion)
        }
    }

    private fun filtrarPromocionesPorExpiracion(promociones: List<PromocionJoven>): List<PromocionJoven> {
        return promociones.filter { promocion ->
            diasHastaExpiracion(promocion.fecha_expiracion) in 1..7
        }
    }

    private fun esPromocionReciente(fechaCreacion: String): Boolean {
        return try {
            val fechaCreacionDate = dateFormatter.parse(fechaCreacion)
            val hoy = Calendar.getInstance().time
            val diferenciaDias = calcularDiferenciaDias(fechaCreacionDate, hoy)
            diferenciaDias <= 10
        } catch (e: Exception) {
            false
        }
    }

    fun diasHastaExpiracion(fechaExpiracion: String): Long {
        return try {
            val fechaExpiracionDate = dateFormatter.parse(fechaExpiracion)
            val hoy = Calendar.getInstance().time
            calcularDiferenciaDias(hoy, fechaExpiracionDate)
        } catch (e: Exception) {
            -1
        }
    }

    private fun calcularDiferenciaDias(fechaInicio: Date, fechaFin: Date): Long {
        val diferenciaMillis = fechaFin.time - fechaInicio.time
        return diferenciaMillis / (1000 * 60 * 60 * 24)
    }

    fun formatearTextoExpiracion(fechaExpiracion: String): String {
        val dias = diasHastaExpiracion(fechaExpiracion)
        return when {
            dias < 0 -> "Expirada"
            dias == 0L -> "Vence hoy"
            dias == 1L -> "Vence en 1 día"
            dias <= 7L -> "Vence en $dias días"
            else -> "Válida"
        }
    }

    fun refrescarPromociones() {
        cargarPromocionesDelBackend()
    }

    fun clearError() {
        _error.value = null
    }

    fun diasDesdeCreacion(fechaCreacion: String): Long {
        return try {
            val calendarioHoy = Calendar.getInstance()
            val calendarioCreacion = Calendar.getInstance()
            val fechaCreacionDate = dateFormatter.parse(fechaCreacion)

            calendarioCreacion.time = fechaCreacionDate
            val diferenciaMillis = calendarioHoy.timeInMillis - calendarioCreacion.timeInMillis
            diferenciaMillis / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            -1
        }
    }
}