package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Categoria
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.ServicioRemoto

class BeneficioJovenVM : ViewModel() {
    private val servicioRemoto = ServicioRemoto

    // State Flows
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _favoritos = MutableStateFlow<List<Promocion>>(emptyList())
    val favoritos: StateFlow<List<Promocion>> = _favoritos.asStateFlow()

    private val _nuevasPromociones = MutableStateFlow<List<Promocion>>(emptyList())
    val nuevasPromociones: StateFlow<List<Promocion>> = _nuevasPromociones.asStateFlow()

    private val _promocionesExpiracion = MutableStateFlow<List<Promocion>>(emptyList())
    val promocionesExpiracion: StateFlow<List<Promocion>> = _promocionesExpiracion.asStateFlow()

    private val _promocionesCercanas = MutableStateFlow<List<Promocion>>(emptyList())
    val promocionesCercanas: StateFlow<List<Promocion>> = _promocionesCercanas.asStateFlow()

    private val _estadoCargando = MutableStateFlow(false)
    val estadoCargando: StateFlow<Boolean> = _estadoCargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarDatosIniciales()
    }

    fun cargarDatosIniciales() {
        _estadoCargando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Cargar todos los datos en paralelo
                val categoriasDeferred = launch {
                    _categorias.value = servicioRemoto.obtenerCategorias()
                }
                val favoritosDeferred = launch {
                    _favoritos.value = servicioRemoto.obtenerFavoritos()
                }
                val nuevasPromoDeferred = launch {
                    _nuevasPromociones.value = servicioRemoto.obtenerNuevasPromociones()
                }
                val expiracionDeferred = launch {
                    _promocionesExpiracion.value = servicioRemoto.obtenerPromocionesPorExpiracion()
                }
                val cercanasDeferred = launch {
                    _promocionesCercanas.value = servicioRemoto.obtenerPromocionesCercanas()
                }
                
                categoriasDeferred.join()
                favoritosDeferred.join()
                nuevasPromoDeferred.join()
                expiracionDeferred.join()
                cercanasDeferred.join()

            } catch (e: Exception) {
                _error.value = "Error al cargar los datos: ${e.message}"
            } finally {
                _estadoCargando.value = false
            }
        }
    }

    fun refrescarDatos() {
        cargarDatosIniciales()
    }

    fun limpiarError() {
        _error.value = null
    }
}