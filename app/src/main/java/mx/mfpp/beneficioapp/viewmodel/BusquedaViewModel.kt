package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Establecimiento
import mx.mfpp.beneficioapp.model.ServicioRemotoEstablecimiento

/**
 * ViewModel para manejar búsqueda y filtrado de establecimientos
 */
class BusquedaViewModel(private val context: Context) : ViewModel() {

    private val _establecimientos = MutableStateFlow<List<Establecimiento>>(emptyList())
    val establecimientos: StateFlow<List<Establecimiento>> = _establecimientos.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada.asStateFlow()

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Lista original completa (sin filtrar)
    private var todosEstablecimientos: List<Establecimiento> = emptyList()

    init {
        cargarEstablecimientos()
    }

    fun cargarEstablecimientos() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                todosEstablecimientos = ServicioRemotoEstablecimiento.obtenerEstablecimientos(context)
                aplicarFiltros()
            } catch (e: Exception) {
                _error.value = "Error al cargar establecimientos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun limpiarBusqueda() {
        _categoriaSeleccionada.value = null
        _textoBusqueda.value = ""
        aplicarFiltros()
    }

    fun clearError() {
        _error.value = null
    }
    fun refrescarEstablecimientos() {
        cargarEstablecimientos()
    }

    fun seleccionarCategoria(categoria: String) {

        Log.d("BUSQUEDA_VM", "🔍 Categoría seleccionada: $categoria")
        Log.d("BUSQUEDA_VM", "📦 Total establecimientos: ${todosEstablecimientos.size}")
        _categoriaSeleccionada.value = categoria
        _textoBusqueda.value = "" // opcional: limpia el texto si seleccionas categoría
        aplicarFiltros()

        Log.d("BUSQUEDA_VM", "✅ Después del filtro: ${_establecimientos.value.size} establecimientos")
    }

    fun actualizarTextoBusqueda(texto: String) {
        _textoBusqueda.value = texto

        // Si estás escribiendo texto, elimina la categoría
        if (texto.isNotEmpty()) {
            _categoriaSeleccionada.value = null
        }

        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val categoria = _categoriaSeleccionada.value
        val texto = _textoBusqueda.value

        Log.d("BUSQUEDA_VM", "🎯 Aplicando filtros - Categoría: $categoria, Texto: $texto")


        val filtrados = todosEstablecimientos.filter { establecimiento ->
            val coincideCategoria = categoria?.let {
                establecimiento.nombre_categoria.equals(it, ignoreCase = true)
            } ?: true

            val coincideTexto = texto.isEmpty() || establecimiento.nombre.contains(texto, ignoreCase = true)
                    || establecimiento.colonia.contains(texto, ignoreCase = true)
                    || establecimiento.nombre_categoria.contains(texto, ignoreCase = true)

            coincideCategoria && coincideTexto
        }

        Log.d("BUSQUEDA_VM", "📊 Filtrados: ${filtrados.size} de ${todosEstablecimientos.size}")

        _establecimientos.value = filtrados
    }

    fun refrescarEstablecimientosConFavoritos(context: Context) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // CON context - para obtener es_favorito correctamente
                todosEstablecimientos = ServicioRemotoEstablecimiento.obtenerEstablecimientos(context)
                aplicarFiltros()
            } catch (e: Exception) {
                _error.value = "Error al cargar establecimientos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
