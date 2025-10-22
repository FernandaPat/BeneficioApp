package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.mode.ServicioRemotoEstablecimiento
import mx.mfpp.beneficioapp.model.Establecimiento

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
        // Carga inicial sin contexto (usa id_usuario = 0)
        cargarEstablecimientos()
    }

    fun cargarEstablecimientos(context: Context? = null) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val nuevosEstablecimientos = ServicioRemotoEstablecimiento.obtenerEstablecimientos(context)

                // Actualizar la lista completa
                todosEstablecimientos = nuevosEstablecimientos

                // Aplicar filtros actuales
                aplicarFiltros()

                Log.d("BUSQUEDA_VM", "✅ Establecimientos cargados: ${nuevosEstablecimientos.size}")

            } catch (e: Exception) {
                _error.value = "Error al cargar establecimientos: ${e.message}"
                Log.e("BUSQUEDA_VM", "❌ Error: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refrescarEstablecimientos(context: Context? = null) {
        Log.d("BUSQUEDA_VM", "🔄 Refrescando establecimientos")
        cargarEstablecimientos(context)
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
        Log.d("BUSQUEDA_VM", "🔍 Categoría seleccionada: $categoria")
        Log.d("BUSQUEDA_VM", "📦 Total establecimientos: ${todosEstablecimientos.size}")
        _categoriaSeleccionada.value = categoria
        _textoBusqueda.value = "" // limpia texto si seleccionas categoría
        aplicarFiltros()

        Log.d("BUSQUEDA_VM", "✅ Después del filtro: ${_establecimientos.value.size} establecimientos")
    }

    fun actualizarTextoBusqueda(texto: String) {
        _textoBusqueda.value = texto

        // Si escribes texto, elimina la categoría
        if (texto.isNotEmpty()) {
            _categoriaSeleccionada.value = null
        }

        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val categoria = _categoriaSeleccionada.value
        val texto = _textoBusqueda.value

        Log.d("BUSQUEDA_VM", "🎯 Aplicando filtros - Categoría: $categoria, Texto: $texto")
        Log.d("BUSQUEDA_VM", "📦 Base de datos: ${todosEstablecimientos.size} establecimientos")

        val filtrados = todosEstablecimientos.filter { establecimiento ->
            val coincideCategoria = categoria?.let {
                establecimiento.nombre_categoria.equals(it, ignoreCase = true)
            } ?: true

            val coincideTexto = texto.isEmpty() ||
                    establecimiento.nombre.contains(texto, ignoreCase = true) ||
                    establecimiento.colonia.contains(texto, ignoreCase = true) ||
                    establecimiento.nombre_categoria.contains(texto, ignoreCase = true)

            coincideCategoria && coincideTexto
        }

        Log.d("BUSQUEDA_VM", "📊 Resultado filtro: ${filtrados.size} establecimientos")

        // Forzar actualización
        _establecimientos.value = filtrados
    }

    fun recargarFavoritos(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                todosEstablecimientos = ServicioRemotoEstablecimiento.obtenerEstablecimientos(context)
                aplicarFiltros()
                Log.d("BUSQUEDA_VM", "✅ Favoritos recargados correctamente")
            } catch (e: Exception) {
                Log.e("BUSQUEDA_VM", "❌ Error recargando favoritos: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarFavoritoLocal(idEstablecimiento: Int, esFavorito: Boolean) {
        Log.d("BUSQUEDA_VM", "🔄 Actualizando favorito local: $idEstablecimiento -> $esFavorito")

        _establecimientos.update { list ->
            list.map { est ->
                if (est.id_establecimiento == idEstablecimiento)
                    est.copy(es_favorito = esFavorito)
                else est
            }
        }

        todosEstablecimientos = todosEstablecimientos.map { est ->
            if (est.id_establecimiento == idEstablecimiento)
                est.copy(es_favorito = esFavorito)
            else est
        }
    }
}