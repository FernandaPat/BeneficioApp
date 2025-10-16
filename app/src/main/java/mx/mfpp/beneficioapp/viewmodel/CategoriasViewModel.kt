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
 * ViewModel para manejar datos de categorías
 */
class CategoriasViewModel : ViewModel() {

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarCategorias()
    }

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

    fun refrescarCategorias() {
        cargarCategorias()
    }

    fun clearError() {
        _error.value = null
    }

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