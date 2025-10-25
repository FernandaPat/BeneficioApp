package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.model.Categoria

/**
 * ViewModel encargado de manejar los datos de categorías.
 *
 * Gestiona la lista de categorías, el estado de carga y errores.
 */
class CategoriasViewModel : ViewModel() {

    /**
     * Lista de categorías disponibles.
     */
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    /**
     * Indica si se está cargando información de categorías.
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Mensaje de error al cargar categorías, si existe alguno.
     */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Carga inicial de categorías
        cargarCategorias()
    }

    /**
     * Carga la lista de categorías desde un origen de datos.
     *
     * En este caso, simula la carga con un delay y genera categorías de ejemplo.
     * Maneja el estado de carga y los posibles errores.
     */
    fun cargarCategorias() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Simular carga de datos
                kotlinx.coroutines.delay(500)
                _categorias.value = generarCategoriasMock()
            } catch (e: Exception) {
                _error.value = "Error al cargar categorías: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresca la lista de categorías recargando los datos.
     */
    fun refrescarCategorias() {
        cargarCategorias()
    }

    /**
     * Limpia cualquier mensaje de error existente.
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Genera una lista simulada de categorías para pruebas o UI mock.
     *
     * @return Lista de objetos [Categoria] con datos de ejemplo.
     */
    private fun generarCategoriasMock(): List<Categoria> {
        return listOf(
            Categoria(1, "Belleza", R.drawable.belleza, "#FF6B9C"),
            Categoria(2, "Comida", R.drawable.comida, "#4CAF50"),
            Categoria(3, "Educación", R.drawable.educacion, "#2196F3"),
            Categoria(4, "Entretenimiento", R.drawable.entretenimiento, "#9C27B0"),
            Categoria(5, "Moda", R.drawable.moda, "#FF9800"),
            Categoria(6, "Salud", R.drawable.salud, "#F44336"),
            Categoria(7, "Servicios", R.drawable.servicios, "#607D8B")
        )
    }
}