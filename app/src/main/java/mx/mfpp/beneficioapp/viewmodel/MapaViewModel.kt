package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Establecimiento
import com.google.android.gms.maps.model.LatLng
import mx.mfpp.beneficioapp.mode.ServicioRemotoEstablecimiento
import kotlin.math.*

// ✅ AGREGADO: Estados específicos para la UI
sealed class MapaUiState {
    object Loading : MapaUiState()
    object Empty : MapaUiState()
    data class Success(val establecimientos: List<Establecimiento>) : MapaUiState()
    data class Error(val message: String) : MapaUiState()
}

class MapaViewModel : ViewModel() {

    private val _establecimientos = MutableStateFlow<List<Establecimiento>>(emptyList())
    val establecimientos: StateFlow<List<Establecimiento>> = _establecimientos.asStateFlow()

    private val _establecimientosFiltrados = MutableStateFlow<List<Establecimiento>>(emptyList())
    val establecimientosFiltrados: StateFlow<List<Establecimiento>> = _establecimientosFiltrados.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Ubicación actual para calcular distancias
    private var _ubicacionActual = MutableStateFlow<LatLng?>(null)
    val ubicacionActual: StateFlow<LatLng?> = _ubicacionActual.asStateFlow()

    // ✅ MEJORADO: Estado más específico para la UI
    private val _uiState = MutableStateFlow<MapaUiState>(MapaUiState.Loading)
    val uiState: StateFlow<MapaUiState> = _uiState.asStateFlow()

    private val _establecimientosOrdenados = MutableStateFlow<List<Establecimiento>>(emptyList())
    val establecimientosOrdenados: StateFlow<List<Establecimiento>> = _establecimientosOrdenados.asStateFlow()

    // Solo establecimientos con coordenadas válidas
    val establecimientosConCoordenadas: List<Establecimiento>
        get() = _establecimientos.value.filter {
            it.latitud != null && it.longitud != null
        }

    // Establecimientos ordenados por distancia (más cercanos primero)
    val establecimientosOrdenadosPorDistancia: List<Establecimiento>
        get() {
            val ubicacion = _ubicacionActual.value
            return if (ubicacion != null) {
                _establecimientosFiltrados.value
                    .filter { it.latitud != null && it.longitud != null }
                    .sortedBy { establecimiento ->
                        calcularDistancia(
                            ubicacion,
                            LatLng(establecimiento.latitud!!, establecimiento.longitud!!)
                        )
                    }
            } else {
                _establecimientosFiltrados.value
            }
        }

    init {
        cargarEstablecimientos()
    }

    fun cargarEstablecimientos() {
        _isLoading.value = true
        _error.value = null
        _uiState.value = MapaUiState.Loading

        viewModelScope.launch {
            try {
                // ✅ MEJORADO: Simular un pequeño retraso para mostrar el loading (opcional)
                kotlinx.coroutines.delay(500)

                val todosEstablecimientos = ServicioRemotoEstablecimiento.obtenerEstablecimientos()

                _establecimientos.value = todosEstablecimientos
                _establecimientosFiltrados.value = todosEstablecimientos
                _establecimientosOrdenados.value = todosEstablecimientos

                // Actualizar estado basado en el resultado
                if (todosEstablecimientos.isEmpty()) {
                    _uiState.value = MapaUiState.Empty
                } else {
                    _uiState.value = MapaUiState.Success(todosEstablecimientos)

                    // ✅ MEJORADO: Ordenar establecimientos si ya tenemos ubicación
                    _ubicacionActual.value?.let { ubicacion ->
                        actualizarUbicacionActual(ubicacion)
                    }
                }

                // 🔧 Delay para asegurar actualización del flujo
                kotlinx.coroutines.delay(300)

            } catch (e: Exception) {
                _error.value = "Error al cargar establecimientos: ${e.message}"
                _uiState.value = MapaUiState.Error(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarUbicacionActual(latLng: LatLng) {
        _ubicacionActual.value = latLng

        // ✅ MEJORADO: Ordenar establecimientos por distancia a la nueva ubicación
        val establecimientosOrdenados = _establecimientosFiltrados.value.sortedBy { establecimiento ->
            if (establecimiento.latitud != null && establecimiento.longitud != null) {
                calcularDistancia(latLng, LatLng(establecimiento.latitud!!, establecimiento.longitud!!))
            } else {
                Double.MAX_VALUE
            }
        }

        _establecimientosOrdenados.value = establecimientosOrdenados
    }

    fun filtrarEstablecimientos(query: String) {
        if (query.isEmpty()) {
            _establecimientosFiltrados.value = _establecimientos.value
            _establecimientosOrdenados.value = _establecimientos.value
        } else {
            val filtrados = _establecimientos.value.filter { establecimiento ->
                establecimiento.nombre.contains(query, ignoreCase = true) ||
                        establecimiento.nombre_categoria.contains(query, ignoreCase = true) ||
                        establecimiento.colonia.contains(query, ignoreCase = true)
            }
            _establecimientosFiltrados.value = filtrados

            // ✅ MEJORADO: Re-ordenar los resultados filtrados
            _ubicacionActual.value?.let { ubicacion ->
                val ordenados = filtrados.sortedBy { establecimiento ->
                    if (establecimiento.latitud != null && establecimiento.longitud != null) {
                        calcularDistancia(ubicacion, LatLng(establecimiento.latitud!!, establecimiento.longitud!!))
                    } else {
                        Double.MAX_VALUE
                    }
                }
                _establecimientosOrdenados.value = ordenados
            } ?: run {
                _establecimientosOrdenados.value = filtrados
            }
        }
    }

    fun refrescarEstablecimientos() {
        cargarEstablecimientos()
    }

    fun clearError() {
        _error.value = null
        // ✅ MEJORADO: Restaurar estado anterior después de limpiar error
        if (_establecimientos.value.isNotEmpty()) {
            _uiState.value = MapaUiState.Success(_establecimientos.value)
        } else if (_establecimientos.value.isEmpty()) {
            _uiState.value = MapaUiState.Empty
        }
    }

    // Función para calcular distancia entre dos puntos en metros
    private fun calcularDistancia(punto1: LatLng, punto2: LatLng): Double {
        val radioTierra = 6371000.0 // Radio de la Tierra en metros

        val lat1 = Math.toRadians(punto1.latitude)
        val lon1 = Math.toRadians(punto1.longitude)
        val lat2 = Math.toRadians(punto2.latitude)
        val lon2 = Math.toRadians(punto2.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radioTierra * c
    }

    // Función para formatear distancia en texto amigable
    fun formatearDistancia(metros: Double): String {
        return when {
            metros < 1000 -> "${metros.toInt()} m"
            else -> "${(metros / 1000).format(1)} km"
        }
    }

    // Extensión para formatear decimales
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}