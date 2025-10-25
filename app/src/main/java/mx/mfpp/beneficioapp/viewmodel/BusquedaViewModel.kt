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
 * ViewModel encargado de manejar la búsqueda y filtrado de establecimientos.
 *
 * Gestiona la lista de establecimientos, filtros por categoría y texto,
 * estado de carga, errores y favoritos locales.
 */
class BusquedaViewModel : ViewModel() {

    /**
     * Lista filtrada de establecimientos para mostrar en la UI.
     */
    private val _establecimientos = MutableStateFlow<List<Establecimiento>>(emptyList())
    val establecimientos: StateFlow<List<Establecimiento>> = _establecimientos.asStateFlow()

    /**
     * Categoría seleccionada para filtrar establecimientos.
     */
    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada.asStateFlow()

    /**
     * Texto de búsqueda ingresado por el usuario para filtrar establecimientos.
     */
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    /**
     * Indica si se está cargando la información de establecimientos.
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Mensaje de error global de la búsqueda, si existe.
     */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Lista completa de establecimientos sin filtrar.
     */
    private var todosEstablecimientos: List<Establecimiento> = emptyList()

    init {
        // Carga inicial sin contexto (usa id_usuario = 0)
        cargarEstablecimientos()
    }

    /**
     * Carga los establecimientos desde el servicio remoto y aplica filtros actuales.
     *
     * @param context Contexto opcional para la carga de datos remotos.
     */
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

    /**
     * Recarga los establecimientos desde el servicio remoto.
     *
     * @param context Contexto opcional para la carga de datos remotos.
     */
    fun refrescarEstablecimientos(context: Context? = null) {
        Log.d("BUSQUEDA_VM", "🔄 Refrescando establecimientos")
        cargarEstablecimientos(context)
    }

    /**
     * Limpia completamente todos los filtros y muestra todos los establecimientos.
     */
    fun limpiarFiltrosCompletamente() {
        Log.d("BUSQUEDA_VM", "🧹 Limpiando TODOS los filtros")
        _categoriaSeleccionada.value = null
        _textoBusqueda.value = ""
        aplicarFiltros()
    }

    /**
     * Limpia los filtros de búsqueda y categoría actuales.
     */
    fun limpiarBusqueda() {
        _categoriaSeleccionada.value = null
        _textoBusqueda.value = ""
        aplicarFiltros()
    }

    /**
     * Limpia el mensaje de error global.
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Selecciona una categoría para filtrar establecimientos y limpia el texto de búsqueda.
     *
     * @param categoria Nombre de la categoría a seleccionar.
     */
    fun seleccionarCategoria(categoria: String) {
        Log.d("BUSQUEDA_VM", "🔍 Categoría seleccionada: $categoria")
        Log.d("BUSQUEDA_VM", "📦 Total establecimientos: ${todosEstablecimientos.size}")
        _categoriaSeleccionada.value = categoria
        _textoBusqueda.value = ""
        aplicarFiltros()

        Log.d("BUSQUEDA_VM", "✅ Después del filtro: ${_establecimientos.value.size} establecimientos")
    }

    /**
     * Actualiza el texto de búsqueda y aplica filtros.
     *
     * Si se ingresa texto, la categoría seleccionada se limpia automáticamente.
     *
     * @param texto Texto ingresado por el usuario.
     */
    fun actualizarTextoBusqueda(texto: String) {
        _textoBusqueda.value = texto

        if (texto.isNotEmpty()) {
            _categoriaSeleccionada.value = null
        }

        aplicarFiltros()
    }

    /**
     * Aplica los filtros de categoría y texto a la lista completa de establecimientos
     * y actualiza la lista filtrada observable [_establecimientos].
     */
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

        _establecimientos.value = filtrados
    }

    /**
     * Recarga los establecimientos desde el servicio remoto específicamente
     * para actualizar favoritos.
     *
     * @param context Contexto necesario para la carga de datos remotos.
     */
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

    /**
     * Actualiza el estado de favorito de un establecimiento localmente,
     * tanto en la lista filtrada como en la lista completa.
     *
     * @param idEstablecimiento ID del establecimiento a actualizar.
     * @param esFavorito Nuevo estado de favorito (true/false).
     */
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