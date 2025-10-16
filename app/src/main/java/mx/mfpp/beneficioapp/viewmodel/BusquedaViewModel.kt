package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        _establecimientos.value = generarEstablecimientosMock()
    }

    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
        _textoBusqueda.value = ""
        filtrarEstablecimientos(categoria, "")
    }

    fun actualizarTextoBusqueda(texto: String) {
        _textoBusqueda.value = texto
        if (texto.isNotEmpty()) {
            _categoriaSeleccionada.value = null
        }
        filtrarEstablecimientos(null, texto)
    }

    fun limpiarBusqueda() {
        _categoriaSeleccionada.value = null
        _textoBusqueda.value = ""
        _establecimientos.value = generarEstablecimientosMock()
    }

    private fun filtrarEstablecimientos(categoria: String?, texto: String) {
        val todosEstablecimientos = generarEstablecimientosMock()

        val establecimientosFiltrados = if (categoria != null && texto.isEmpty()) {
            todosEstablecimientos.filter { establecimiento ->
                establecimiento.categoria.equals(categoria, ignoreCase = true)
            }
        } else if (texto.isNotEmpty() && categoria == null) {
            todosEstablecimientos.filter { establecimiento ->
                establecimiento.nombre.contains(texto, ignoreCase = true) ||
                        establecimiento.categoria.contains(texto, ignoreCase = true)
            }
        } else {
            todosEstablecimientos
        }

        _establecimientos.value = establecimientosFiltrados
    }

    private fun generarEstablecimientosMock(): List<Establecimiento> {
        return listOf(
            Establecimiento("1", "Spa Relajante", "Belleza", "10 min", "1.6 km", "https://picsum.photos/200/300?random=1", 4.8, false),
            Establecimiento("2", "Salón de Belleza Glam", "Belleza", "15 min", "2.1 km", "https://picsum.photos/200/300?random=2", 4.5, true),
            Establecimiento("3", "Pizzería Italiana", "Comida", "8 min", "1.2 km", "https://picsum.photos/200/300?random=3", 4.7, false),
            Establecimiento("4", "Restaurante Sushi Tokyo", "Comida", "12 min", "1.8 km", "https://picsum.photos/200/300?random=4", 4.4, false),
            Establecimiento("5", "Academia de Inglés", "Educación", "20 min", "3.2 km", "https://picsum.photos/200/300?random=5", 4.6, false),
            Establecimiento("6", "Cine Premium", "Entretenimiento", "5 min", "0.8 km", "https://picsum.photos/200/300?random=6", 4.3, true),
            Establecimiento("7", "Boutique Moda", "Moda", "18 min", "2.5 km", "https://picsum.photos/200/300?random=7", 4.2, false),
            Establecimiento("8", "Clínica Dental", "Salud", "25 min", "3.8 km", "https://picsum.photos/200/300?random=8", 4.9, false),
            Establecimiento("9", "Taller Mecánico", "Servicios", "30 min", "4.2 km", "https://picsum.photos/200/300?random=9", 4.1, false),
            Establecimiento("10", "Sushi Bar Premium", "Comida", "7 min", "1.0 km", "https://picsum.photos/200/300?random=10", 4.8, false),
            Establecimiento("11", "Centro de Estética", "Belleza", "12 min", "1.9 km", "https://picsum.photos/200/300?random=11", 4.6, false)
        )
    }
}