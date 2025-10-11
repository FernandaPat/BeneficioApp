package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.model.Categoria
import mx.mfpp.beneficioapp.model.Establecimiento
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.QrScanResult

class BeneficioJovenVM : ViewModel() {

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

        private val _qrScanResults = MutableStateFlow<List<QrScanResult>>(emptyList())
        val qrScanResults: StateFlow<List<QrScanResult>> = _qrScanResults.asStateFlow()

        private val _showScanner = MutableStateFlow(false)
        val showScanner: StateFlow<Boolean> = _showScanner.asStateFlow()

        // State Flows para búsqueda (SIMPLE)
        private val _establecimientos = MutableStateFlow<List<Establecimiento>>(emptyList())
        val establecimientos: StateFlow<List<Establecimiento>> = _establecimientos.asStateFlow()

        private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
        val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada.asStateFlow()

        private val _textoBusqueda = MutableStateFlow("")
        val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

        // Funciones del scanner (igual)
        fun hideScanner() {
            _showScanner.value = false
        }

        fun showScanner() {
            _showScanner.value = true
        }

        fun resetScannerState() {
            _showScanner.value = false
        }

        fun addQrScanResult(content: String) {
            val newList = _qrScanResults.value.toMutableList()
            newList.add(QrScanResult(content))
            _qrScanResults.value = newList
            hideScanner()
        }

        fun deleteQrScanResult(result: QrScanResult) {
            val newList = _qrScanResults.value.toMutableList()
            newList.remove(result)
            _qrScanResults.value = newList
        }

        fun getTotalScans(): Int {
            return _qrScanResults.value.size
        }

        // Funciones de búsqueda (SIMPLE - una sola categoría)
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
                // Filtrar por categoría
                todosEstablecimientos.filter { establecimiento ->
                    establecimiento.categoria.equals(categoria, ignoreCase = true)
                }
            } else if (texto.isNotEmpty() && categoria == null) {
                // Filtrar por texto
                todosEstablecimientos.filter { establecimiento ->
                    establecimiento.nombre.contains(texto, ignoreCase = true) ||
                            establecimiento.categoria.contains(texto, ignoreCase = true)
                }
            } else {
                // Sin filtros
                todosEstablecimientos
            }

            _establecimientos.value = establecimientosFiltrados
        }

        init {
            cargarDatosIniciales()
            _establecimientos.value = generarEstablecimientosMock()
        }

    fun cargarDatosIniciales() {
        _estadoCargando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(1000)
                _categorias.value = generarCategoriasMock()
                _favoritos.value = generarFavoritosMock()
                _nuevasPromociones.value = generarNuevasPromocionesMock()
                _promocionesExpiracion.value = generarPromocionesExpiracionMock()
                _promocionesCercanas.value = generarPromocionesCercanasMock()
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

    // Datos Mock
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

    private fun generarFavoritosMock(): List<Promocion> {
        return listOf(
            Promocion(1, "Spa Relajante", "https://picsum.photos/200/300?random=1", "30% OFF", "Belleza", 5, "2.3 km", true, 4.8, "Día de spa completo con masaje relajante"),
            Promocion(2, "Pizzería Italiana", "https://picsum.photos/200/300?random=2", "2x1", "Comida", 3, "1.5 km", true, 4.5, "Pizzas artesanales con ingredientes frescos"),
            Promocion(3, "Cine Premium", "https://picsum.photos/200/300?random=3", "25% OFF", "Entretenimiento", 7, "3.2 km", true, 4.3, "Entradas para estreno exclusivo")
        )
    }

    private fun generarNuevasPromocionesMock(): List<Promocion> {
        return listOf(
            Promocion(4, "Curso Online", "https://picsum.photos/200/300?random=4", "50% OFF", "Educación", 30, "Online", false, 4.7, "Curso completo de desarrollo móvil"),
            Promocion(5, "Boutique Moda", "https://picsum.photos/200/300?random=5", "40% OFF", "Moda", 15, "1.8 km", false, 4.6, "Ropa de temporada con descuento"),
            Promocion(6, "Restaurante Sushi", "https://picsum.photos/200/300?random=6", "20% OFF", "Comida", 10, "2.5 km", false, 4.4, "Sushi fresco con descuento especial")
        )
    }

    private fun generarPromocionesExpiracionMock(): List<Promocion> {
        return listOf(
            Promocion(7, "Gimnasio Fit", "https://picsum.photos/200/300?random=7", "40% OFF", "Salud", 1, "0.8 km", false, 4.6, "Membresía mensual con acceso completo"),
            Promocion(8, "Taller Mecánico", "https://picsum.photos/200/300?random=8", "15% OFF", "Servicios", 2, "1.2 km", false, 4.2, "Servicio de mantenimiento vehicular")
        )
    }

    private fun generarPromocionesCercanasMock(): List<Promocion> {
        return listOf(
            Promocion(9, "Cafetería Central", "https://picsum.photos/200/300?random=9", "Café Gratis", "Comida", 7, "0.5 km", false, 4.4, "Café gratis con cualquier compra"),
            Promocion(10, "Farmacia 24/7", "https://picsum.photos/200/300?random=10", "10% OFF", "Salud", 14, "0.3 km", false, 4.1, "Descuento en productos de farmacia"),
            Promocion(11, "Lavandería Express", "https://picsum.photos/200/300?random=11", "2x1", "Servicios", 21, "0.7 km", false, 4.3, "Servicio de lavandería express")
        )
    }
}