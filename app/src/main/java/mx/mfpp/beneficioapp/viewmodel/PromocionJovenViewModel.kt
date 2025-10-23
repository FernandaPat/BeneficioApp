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

    private val _todasPromociones = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val todasPromociones: StateFlow<List<PromocionJoven>> = _todasPromociones.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("America/Mexico_City")
    }

    init {
        cargarPromocionesDelBackend()
    }

    fun cargarPromocionesDelBackend() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                println("🔄 Cargando promociones desde la nueva API...")
                val promocionesReales = ServicioRemotoJovenesPromocion.obtenerPromociones()

                println("✅ Promociones recibidas: ${promocionesReales.size}")
                promocionesReales.take(3).forEach { promocion ->
                    println("   - ${promocion.titulo} (${promocion.nombre_establecimiento})")
                }

                // Filtrar promociones activas
                val promocionesActivas = promocionesReales.filter { it.estado == "activa" }
                println("✅ Promociones activas: ${promocionesActivas.size}")

                // Aplicar lógica de filtrado
                _nuevasPromociones.value = filtrarNuevasPromociones(promocionesActivas)
                _promocionesExpiracion.value = filtrarPromocionesPorExpiracion(promocionesActivas)
                _todasPromociones.value = promocionesActivas

                println("✅ Filtrado completado:")
                println("   - Nuevas: ${_nuevasPromociones.value.size}")
                println("   - Por expirar: ${_promocionesExpiracion.value.size}")
                println("   - Todas: ${_todasPromociones.value.size}")

            } catch (e: Exception) {
                println("❌ Error al cargar promociones: ${e.message}")
                _error.value = "Error al cargar promociones: ${e.message}"
                e.printStackTrace()
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
            diasHastaExpiracion(promocion.fecha_expiracion) in 1..15
        }
    }

    private fun esPromocionReciente(fechaCreacion: String): Boolean {
        return try {
            val fechaCreacionDate = dateFormatter.parse(fechaCreacion)
            val hoy = obtenerFechaActualSinHora()
            val diferenciaDias = calcularDiferenciaDias(fechaCreacionDate, hoy)
            diferenciaDias <= 10
        } catch (e: Exception) {
            false
        }
    }

    fun diasHastaExpiracion(fechaExpiracion: String): Long {
        return try {
            val fechaExpiracionDate = dateFormatter.parse(fechaExpiracion)
            val hoy = obtenerFechaActualSinHora()
            calcularDiferenciaDias(hoy, fechaExpiracionDate)
        } catch (e: Exception) {
            -1
        }
    }

    private fun calcularDiferenciaDias(fechaInicio: Date, fechaFin: Date): Long {
        val calInicio = Calendar.getInstance().apply {
            time = fechaInicio
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val calFin = Calendar.getInstance().apply {
            time = fechaFin
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diferenciaMillis = calFin.timeInMillis - calInicio.timeInMillis
        return diferenciaMillis / (1000 * 60 * 60 * 24)
    }

    private fun obtenerFechaActualSinHora(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun formatearTextoExpiracion(fechaExpiracion: String): String {
        val dias = diasHastaExpiracion(fechaExpiracion)
        return when {
            dias < 0 -> "Expirada"
            dias == 0L -> "Vence hoy"
            dias == 1L -> "Vence en 1 día"
            dias <= 20L -> "Vence en $dias días"
            else -> "Válida"
        }
    }

    fun refrescarPromociones() {
        cargarPromocionesDelBackend()
    }

    fun clearError() {
        _error.value = null
    }
}