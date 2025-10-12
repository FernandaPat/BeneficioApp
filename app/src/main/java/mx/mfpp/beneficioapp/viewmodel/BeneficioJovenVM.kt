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

/**
 * ViewModel principal para la aplicación Beneficio Joven.
 *
 * Gestiona el estado de la UI, datos de establecimientos, promociones, categorías
 * y funcionalidades de escaneo QR.
 *
 * @property categorias Lista de categorías disponibles en la aplicación
 * @property favoritos Lista de promociones marcadas como favoritas
 * @property nuevasPromociones Lista de promociones recientemente agregadas
 * @property promocionesExpiracion Lista de promociones próximas a expirar
 * @property promocionesCercanas Lista de promociones cercanas a la ubicación del usuario
 * @property estadoCargando Indica si los datos están siendo cargados
 * @property error Mensaje de error si ocurre algún problema
 * @property qrScanResults Lista de resultados de escaneos QR realizados
 * @property showScanner Indica si el escáner QR debe mostrarse
 * @property establecimientos Lista de establecimientos filtrados según búsqueda
 * @property categoriaSeleccionada Categoría actualmente seleccionada para filtrado
 * @property textoBusqueda Texto de búsqueda ingresado por el usuario
 */
class BeneficioJovenVM : ViewModel() {

    // State Flows
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())

    /** Flujo de lista de categorías disponibles */
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _favoritos = MutableStateFlow<List<Promocion>>(emptyList())

    /** Flujo de promociones marcadas como favoritas */
    val favoritos: StateFlow<List<Promocion>> = _favoritos.asStateFlow()

    private val _nuevasPromociones = MutableStateFlow<List<Promocion>>(emptyList())

    /** Flujo de promociones recientemente agregadas */
    val nuevasPromociones: StateFlow<List<Promocion>> = _nuevasPromociones.asStateFlow()

    private val _promocionesExpiracion = MutableStateFlow<List<Promocion>>(emptyList())

    /** Flujo de promociones próximas a expirar */
    val promocionesExpiracion: StateFlow<List<Promocion>> = _promocionesExpiracion.asStateFlow()

    private val _promocionesCercanas = MutableStateFlow<List<Promocion>>(emptyList())

    /** Flujo de promociones cercanas a la ubicación del usuario */
    val promocionesCercanas: StateFlow<List<Promocion>> = _promocionesCercanas.asStateFlow()

    private val _estadoCargando = MutableStateFlow(false)

    /** Flujo que indica si los datos están siendo cargados */
    val estadoCargando: StateFlow<Boolean> = _estadoCargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)

    /** Flujo de mensajes de error */
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _qrScanResults = MutableStateFlow<List<QrScanResult>>(emptyList())

    /** Flujo de resultados de escaneos QR */
    val qrScanResults: StateFlow<List<QrScanResult>> = _qrScanResults.asStateFlow()

    private val _showScanner = MutableStateFlow(false)

    /** Flujo que controla la visibilidad del escáner QR */
    val showScanner: StateFlow<Boolean> = _showScanner.asStateFlow()

    // State Flows para búsqueda (SIMPLE)
    private val _establecimientos = MutableStateFlow<List<Establecimiento>>(emptyList())

    /** Flujo de establecimientos filtrados según búsqueda */
    val establecimientos: StateFlow<List<Establecimiento>> = _establecimientos.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)

    /** Flujo de la categoría actualmente seleccionada */
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada.asStateFlow()

    private val _textoBusqueda = MutableStateFlow("")

    /** Flujo del texto de búsqueda actual */
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    // Funciones del scanner

    /**
     * Oculta el escáner QR.
     */
    fun hideScanner() {
        _showScanner.value = false
    }

    /**
     * Muestra el escáner QR.
     */
    fun showScanner() {
        _showScanner.value = true
    }

    /**
     * Reinicia el estado del escáner a oculto.
     */
    fun resetScannerState() {
        _showScanner.value = false
    }

    /**
     * Agrega un resultado de escaneo QR a la lista.
     *
     * @param content Contenido del código QR escaneado
     */
    fun addQrScanResult(content: String) {
        val newList = _qrScanResults.value.toMutableList()
        newList.add(QrScanResult(content))
        _qrScanResults.value = newList
        hideScanner()
    }

    /**
     * Elimina un resultado de escaneo QR de la lista.
     *
     * @param result Resultado QR a eliminar
     */
    fun deleteQrScanResult(result: QrScanResult) {
        val newList = _qrScanResults.value.toMutableList()
        newList.remove(result)
        _qrScanResults.value = newList
    }

    /**
     * Obtiene el número total de escaneos QR realizados.
     *
     * @return Número total de escaneos
     */
    fun getTotalScans(): Int {
        return _qrScanResults.value.size
    }

    // Funciones de búsqueda (SIMPLE - una sola categoría)

    /**
     * Selecciona una categoría para filtrar establecimientos.
     *
     * @param categoria Nombre de la categoría a seleccionar
     */
    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
        _textoBusqueda.value = ""
        filtrarEstablecimientos(categoria, "")
    }

    /**
     * Actualiza el texto de búsqueda y filtra establecimientos.
     *
     * @param texto Texto de búsqueda ingresado por el usuario
     */
    fun actualizarTextoBusqueda(texto: String) {
        _textoBusqueda.value = texto
        if (texto.isNotEmpty()) {
            _categoriaSeleccionada.value = null
        }
        filtrarEstablecimientos(null, texto)
    }

    /**
     * Limpia todos los filtros de búsqueda y muestra todos los establecimientos.
     */
    fun limpiarBusqueda() {
        _categoriaSeleccionada.value = null
        _textoBusqueda.value = ""
        _establecimientos.value = generarEstablecimientosMock()
    }

    /**
     * Filtra establecimientos según categoría y/o texto de búsqueda.
     *
     * @param categoria Categoría para filtrar (opcional)
     * @param texto Texto para buscar en nombre y categoría (opcional)
     */
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

    /**
     * Carga los datos iniciales de la aplicación.
     *
     * Incluye categorías, favoritos, promociones y establecimientos.
     * Maneja estados de carga y errores.
     */
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

    /**
     * Recarga los datos de la aplicación.
     */
    fun refrescarDatos() {
        cargarDatosIniciales()
    }

    /**
     * Limpia cualquier mensaje de error actual.
     */
    fun limpiarError() {
        _error.value = null
    }

    // Datos Mock

    /**
     * Genera datos mock de categorías.
     *
     * @return Lista de categorías mock
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

    /**
     * Genera datos mock de establecimientos.
     *
     * @return Lista de establecimientos mock
     */
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

    /**
     * Genera datos mock de promociones favoritas.
     *
     * @return Lista de promociones favoritas mock
     */
    private fun generarFavoritosMock(): List<Promocion> {
        return listOf(
            Promocion(1, "Spa Relajante", "https://picsum.photos/200/300?random=1", "30% OFF", "Belleza", 5, "2.3 km", true, 4.8, "Día de spa completo con masaje relajante"),
            Promocion(2, "Pizzería Italiana", "https://picsum.photos/200/300?random=2", "2x1", "Comida", 3, "1.5 km", true, 4.5, "Pizzas artesanales con ingredientes frescos"),
            Promocion(3, "Cine Premium", "https://picsum.photos/200/300?random=3", "25% OFF", "Entretenimiento", 7, "3.2 km", true, 4.3, "Entradas para estreno exclusivo")
        )
    }

    /**
     * Genera datos mock de nuevas promociones.
     *
     * @return Lista de nuevas promociones mock
     */
    private fun generarNuevasPromocionesMock(): List<Promocion> {
        return listOf(
            Promocion(4, "Curso Online", "https://picsum.photos/200/300?random=4", "50% OFF", "Educación", 30, "Online", false, 4.7, "Curso completo de desarrollo móvil"),
            Promocion(5, "Boutique Moda", "https://picsum.photos/200/300?random=5", "40% OFF", "Moda", 15, "1.8 km", false, 4.6, "Ropa de temporada con descuento"),
            Promocion(6, "Restaurante Sushi", "https://picsum.photos/200/300?random=6", "20% OFF", "Comida", 10, "2.5 km", false, 4.4, "Sushi fresco con descuento especial")
        )
    }

    /**
     * Genera datos mock de promociones próximas a expirar.
     *
     * @return Lista de promociones próximas a expirar mock
     */
    private fun generarPromocionesExpiracionMock(): List<Promocion> {
        return listOf(
            Promocion(7, "Gimnasio Fit", "https://picsum.photos/200/300?random=7", "40% OFF", "Salud", 1, "0.8 km", false, 4.6, "Membresía mensual con acceso completo"),
            Promocion(8, "Taller Mecánico", "https://picsum.photos/200/300?random=8", "15% OFF", "Servicios", 2, "1.2 km", false, 4.2, "Servicio de mantenimiento vehicular")
        )
    }

    /**
     * Genera datos mock de promociones cercanas.
     *
     * @return Lista de promociones cercanas mock
     */
    private fun generarPromocionesCercanasMock(): List<Promocion> {
        return listOf(
            Promocion(9, "Cafetería Central", "https://picsum.photos/200/300?random=9", "Café Gratis", "Comida", 7, "0.5 km", false, 4.4, "Café gratis con cualquier compra"),
            Promocion(10, "Farmacia 24/7", "https://picsum.photos/200/300?random=10", "10% OFF", "Salud", 14, "0.3 km", false, 4.1, "Descuento en productos de farmacia"),
            Promocion(11, "Lavandería Express", "https://picsum.photos/200/300?random=11", "2x1", "Servicios", 21, "0.7 km", false, 4.3, "Servicio de lavandería express")
        )
    }
}