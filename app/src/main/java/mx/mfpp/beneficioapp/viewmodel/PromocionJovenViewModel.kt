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

/**
 * ViewModel para manejar las promociones dirigidas a jóvenes.
 *
 * Filtra las promociones en tres categorías:
 *  - Nuevas promociones (recientes)
 *  - Promociones próximas a expirar (1-15 días)
 *  - Todas las promociones activas
 */
class PromocionJovenViewModel : ViewModel() {

    /** Promociones recientes */
    private val _nuevasPromociones = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val nuevasPromociones: StateFlow<List<PromocionJoven>> = _nuevasPromociones.asStateFlow()

    /** Promociones próximas a expirar */
    private val _promocionesExpiracion = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val promocionesExpiracion: StateFlow<List<PromocionJoven>> = _promocionesExpiracion.asStateFlow()

    /** Todas las promociones activas */
    private val _todasPromociones = MutableStateFlow<List<PromocionJoven>>(emptyList())
    val todasPromociones: StateFlow<List<PromocionJoven>> = _todasPromociones.asStateFlow()

    /** Indica si se están cargando las promociones */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Mensaje de error, si ocurre alguno */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Formateador de fechas para la API */
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("America/Mexico_City")
    }

    init {
        cargarPromocionesDelBackend()
    }

    /**
     * Carga las promociones desde el backend y actualiza los filtros.
     */
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

                val promocionesActivas = promocionesReales.filter { it.estado == "activa" }
                println("✅ Promociones activas: ${promocionesActivas.size}")

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

    /** Filtra las promociones recientes (creadas en los últimos 10 días) */
    private fun filtrarNuevasPromociones(promociones: List<PromocionJoven>): List<PromocionJoven> =
        promociones.filter { esPromocionReciente(it.fecha_creacion) }

    /** Filtra las promociones que expiran en los próximos 1-15 días */
    private fun filtrarPromocionesPorExpiracion(promociones: List<PromocionJoven>): List<PromocionJoven> =
        promociones.filter { diasHastaExpiracion(it.fecha_expiracion) in 1..15 }

    /** Determina si una promoción es reciente (<= 10 días) */
    private fun esPromocionReciente(fechaCreacion: String): Boolean =
        try {
            val fechaCreacionDate = dateFormatter.parse(fechaCreacion)
            val hoy = obtenerFechaActualSinHora()
            val diferenciaDias = calcularDiferenciaDias(fechaCreacionDate, hoy)
            diferenciaDias <= 10
        } catch (e: Exception) {
            false
        }

    /**
     * Calcula los días restantes hasta la expiración de una promoción.
     * @return Número de días; -1 si hay error.
     */
    fun diasHastaExpiracion(fechaExpiracion: String): Long =
        try {
            val fechaExpiracionDate = dateFormatter.parse(fechaExpiracion)
            val hoy = obtenerFechaActualSinHora()
            calcularDiferenciaDias(hoy, fechaExpiracionDate)
        } catch (e: Exception) {
            -1
        }

    /** Calcula la diferencia en días entre dos fechas */
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

    /** Obtiene la fecha actual sin considerar la hora */
    private fun obtenerFechaActualSinHora(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    /**
     * Formatea un mensaje de expiración amigable.
     * @param fechaExpiracion Fecha de expiración de la promoción
     * @return Texto indicando el estado de expiración
     */
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

    /** Refresca las promociones desde el backend */
    fun refrescarPromociones() {
        cargarPromocionesDelBackend()
    }

    /** Limpia el mensaje de error */
    fun clearError() {
        _error.value = null
    }
}