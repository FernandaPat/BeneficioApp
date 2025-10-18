package mx.mfpp.beneficioapp.viewmodel

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
class BusquedaViewModel : ViewModel() {

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
                println("🔄 Cargando establecimientos desde API...")
                todosEstablecimientos = ServicioRemotoEstablecimiento.obtenerEstablecimientos()
                aplicarFiltros()
                println("✅ Establecimientos cargados y filtrados: ${_establecimientos.value.size}")
            } catch (e: Exception) {
                _error.value = "Error al cargar establecimientos: ${e.message}"
                println("❌ Error cargando establecimientos: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refrescarEstablecimientos() {
        cargarEstablecimientos() // ya incluye aplicarFiltros()
    }

    fun limpiarBusqueda() {
        _categoriaSeleccionada.value = null
        _textoBusqueda.value = ""
        aplicarFiltros()
    }

    fun clearError() {
        _error.value = null
    }

    fun seleccionarCategoria(categoria: String) {
        println("🔄 seleccionarCategoria: $categoria")
        _categoriaSeleccionada.value = categoria
        _textoBusqueda.value = "" // opcional: limpia el texto si seleccionas categoría
        aplicarFiltros()
    }

    fun actualizarTextoBusqueda(texto: String) {
        println("🔄 actualizarTextoBusqueda: $texto")
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

        println("🔍 Aplicando filtros:")
        println("   - Categoría: $categoria")
        println("   - Texto: $texto")

        val filtrados = todosEstablecimientos.filter { establecimiento ->
            val coincideCategoria = categoria?.let {
                establecimiento.nombre_categoria.equals(it, ignoreCase = true)
            } ?: true

            val coincideTexto = texto.isEmpty() || establecimiento.nombre.contains(texto, ignoreCase = true)
                    || establecimiento.colonia.contains(texto, ignoreCase = true)
                    || establecimiento.nombre_categoria.contains(texto, ignoreCase = true)

            coincideCategoria && coincideTexto
        }

        println("✅ Resultados filtrados: ${filtrados.size}")
        _establecimientos.value = filtrados
    }
}
