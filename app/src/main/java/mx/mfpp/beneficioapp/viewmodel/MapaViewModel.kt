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

/**
 * Estados de UI para el mapa de establecimientos.
 */
sealed class MapaUiState {
    /** Estado de carga inicial */
    object Loading : MapaUiState()

    /** Estado cuando no hay establecimientos disponibles */
    object Empty : MapaUiState()

    /**
     * Estado exitoso con establecimientos cargados
     * @param establecimientos Lista de establecimientos obtenidos
     */
    data class Success(val establecimientos: List<Establecimiento>) : MapaUiState()

    /**
     * Estado de error al cargar establecimientos
     * @param message Mensaje de error
     */
    data class Error(val message: String) : MapaUiState()
}

/**
 * ViewModel que maneja la lógica de los establecimientos en un mapa.
 *
 * Permite cargar, filtrar y ordenar establecimientos por distancia,
 * manejar errores, estado de carga y la ubicación actual.
 */
class MapaViewModel : ViewModel() {

    /** Lista completa de establecimientos */
    private val _establecimientos = MutableStateFlow<List<Establecimiento>>(emptyList())
    val establecimientos: StateFlow<List<Establecimiento>> = _establecimientos.asStateFlow()

    /** Lista filtrada de establecimientos según búsqueda */
    private val _establecimientosFiltrados = MutableStateFlow<List<Establecimiento>>(emptyList())
    val establecimientosFiltrados: StateFlow<List<Establecimiento>> = _establecimientosFiltrados.asStateFlow()

    /** Indica si se está cargando información */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Mensaje de error actual */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Ubicación actual del usuario para calcular distancias */
    private var _ubicacionActual = MutableStateFlow<LatLng?>(null)
    val ubicacionActual: StateFlow<LatLng?> = _ubicacionActual.asStateFlow()

    /** Estado de UI específico para la vista del mapa */
    private val _uiState = MutableStateFlow<MapaUiState>(MapaUiState.Loading)
    val uiState: StateFlow<MapaUiState> = _uiState.asStateFlow()

    /** Lista de establecimientos ordenados (generalmente por distancia) */
    private val _establecimientosOrdenados = MutableStateFlow<List<Establecimiento>>(emptyList())
    val establecimientosOrdenados: StateFlow<List<Establecimiento>> = _establecimientosOrdenados.asStateFlow()

    /** Retorna solo los establecimientos que tienen coordenadas válidas */
    val establecimientosConCoordenadas: List<Establecimiento>
        get() = _establecimientos.value.filter {
            it.latitud != null && it.longitud != null
        }

    /** Retorna establecimientos filtrados y ordenados por distancia a la ubicación actual */
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

    /**
     * Carga los establecimientos desde el servicio remoto.
     *
     * Actualiza los estados de UI y las listas internas.
     */
    fun cargarEstablecimientos() {
        _isLoading.value = true
        _error.value = null
        _uiState.value = MapaUiState.Loading

        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(500) // Simula carga

                val todosEstablecimientos = ServicioRemotoEstablecimiento.obtenerEstablecimientos()

                _establecimientos.value = todosEstablecimientos
                _establecimientosFiltrados.value = todosEstablecimientos
                _establecimientosOrdenados.value = todosEstablecimientos

                if (todosEstablecimientos.isEmpty()) {
                    _uiState.value = MapaUiState.Empty
                } else {
                    _uiState.value = MapaUiState.Success(todosEstablecimientos)
                    _ubicacionActual.value?.let { actualizarUbicacionActual(it) }
                }

                kotlinx.coroutines.delay(300) // Delay opcional para actualizar flujos

            } catch (e: Exception) {
                _error.value = "Error al cargar establecimientos: ${e.message}"
                _uiState.value = MapaUiState.Error(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza la ubicación actual y ordena los establecimientos por distancia.
     *
     * @param latLng Nueva ubicación del usuario
     */
    fun actualizarUbicacionActual(latLng: LatLng) {
        _ubicacionActual.value = latLng

        val establecimientosOrdenados = _establecimientosFiltrados.value.sortedBy { establecimiento ->
            if (establecimiento.latitud != null && establecimiento.longitud != null) {
                calcularDistancia(latLng, LatLng(establecimiento.latitud!!, establecimiento.longitud!!))
            } else {
                Double.MAX_VALUE
            }
        }

        _establecimientosOrdenados.value = establecimientosOrdenados
    }

    /**
     * Filtra los establecimientos según una consulta de texto.
     *
     * @param query Texto para filtrar por nombre, categoría o colonia
     */
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

            _ubicacionActual.value?.let { ubicacion ->
                _establecimientosOrdenados.value = filtrados.sortedBy { establecimiento ->
                    if (establecimiento.latitud != null && establecimiento.longitud != null) {
                        calcularDistancia(ubicacion, LatLng(establecimiento.latitud!!, establecimiento.longitud!!))
                    } else {
                        Double.MAX_VALUE
                    }
                }
            } ?: run {
                _establecimientosOrdenados.value = filtrados
            }
        }
    }

    /** Refresca los establecimientos recargando los datos */
    fun refrescarEstablecimientos() {
        cargarEstablecimientos()
    }

    /** Limpia el mensaje de error y actualiza el estado de UI según corresponda */
    fun clearError() {
        _error.value = null
        if (_establecimientos.value.isNotEmpty()) {
            _uiState.value = MapaUiState.Success(_establecimientos.value)
        } else {
            _uiState.value = MapaUiState.Empty
        }
    }

    /**
     * Calcula la distancia entre dos puntos en metros usando la fórmula de Haversine.
     *
     * @param punto1 Primer punto
     * @param punto2 Segundo punto
     * @return Distancia en metros
     */
    private fun calcularDistancia(punto1: LatLng, punto2: LatLng): Double {
        val radioTierra = 6371000.0

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

    /**
     * Formatea una distancia en metros a un string amigable (m o km).
     *
     * @param metros Distancia en metros
     * @return Distancia formateada
     */
    fun formatearDistancia(metros: Double): String {
        return when {
            metros < 1000 -> "${metros.toInt()} m"
            else -> "${(metros / 1000).format(1)} km"
        }
    }

    /** Extensión para formatear Double con N dígitos decimales */
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}