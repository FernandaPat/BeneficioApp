package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.model.Categoria
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
        hideScanner() // Ocultar scanner después de escanear
    }

    fun deleteQrScanResult(result: QrScanResult) {
        val newList = _qrScanResults.value.toMutableList()
        newList.remove(result)
        _qrScanResults.value = newList
    }

    fun getTotalScans(): Int {
        return _qrScanResults.value.size
    }

    init {
        cargarDatosIniciales()
    }

    fun cargarDatosIniciales() {
        _estadoCargando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Simular carga de datos
                kotlinx.coroutines.delay(1000)

                // Cargar datos mock
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

    // Datos Mock para la pantalla de inicio
    private fun generarCategoriasMock(): List<Categoria> {
        return listOf(
            Categoria(1, "Belleza", R.drawable.belleza,"#FF6B9C"), // Reemplaza con tu imagen
            Categoria(2, "Comida", R.drawable.comida, "#4CAF50"),   // Reemplaza con tu imagen
            Categoria(3, "Educación", R.drawable.educacion, "#2196F3"), // Reemplaza con tu imagen
            Categoria(4, "Entretenimiento", R.drawable.entretenimiento, "#9C27B0"), // Reemplaza con tu imagen
            Categoria(5, "Moda", R.drawable.moda, "#FF9800"),       // Reemplaza con tu imagen
            Categoria(6, "Salud", R.drawable.salud, "#F44336"),     // Reemplaza con tu imagen
            Categoria(7, "Servicios", R.drawable.servicios, "#607D8B") // Reemplaza con tu imagen
        )
    }

    private fun generarFavoritosMock(): List<Promocion> {
        return listOf(
            Promocion(
                id = 1,
                nombre = "Spa Relajante",
                imagenUrl = "https://picsum.photos/200/300?random=1",
                descuento = "30% OFF",
                categoria = "Belleza",
                expiraEn = 5,
                ubicacion = "2.3 km",
                esFavorito = true,
                rating = 4.8,
                descripcion = "Día de spa completo con masaje relajante"
            ),
            Promocion(
                id = 2,
                nombre = "Pizzería Italiana",
                imagenUrl = "https://picsum.photos/200/300?random=2",
                descuento = "2x1",
                categoria = "Comida",
                expiraEn = 3,
                ubicacion = "1.5 km",
                esFavorito = true,
                rating = 4.5,
                descripcion = "Pizzas artesanales con ingredientes frescos"
            ),
            Promocion(
                id = 3,
                nombre = "Cine Premium",
                imagenUrl = "https://picsum.photos/200/300?random=3",
                descuento = "25% OFF",
                categoria = "Entretenimiento",
                expiraEn = 7,
                ubicacion = "3.2 km",
                esFavorito = true,
                rating = 4.3,
                descripcion = "Entradas para estreno exclusivo"
            )
        )
    }

    private fun generarNuevasPromocionesMock(): List<Promocion> {
        return listOf(
            Promocion(
                id = 4,
                nombre = "Curso Online",
                imagenUrl = "https://picsum.photos/200/300?random=4",
                descuento = "50% OFF",
                categoria = "Educación",
                expiraEn = 30,
                ubicacion = "Online",
                esFavorito = false,
                rating = 4.7,
                descripcion = "Curso completo de desarrollo móvil"
            ),
            Promocion(
                id = 5,
                nombre = "Boutique Moda",
                imagenUrl = "https://picsum.photos/200/300?random=5",
                descuento = "40% OFF",
                categoria = "Moda",
                expiraEn = 15,
                ubicacion = "1.8 km",
                esFavorito = false,
                rating = 4.6,
                descripcion = "Ropa de temporada con descuento"
            ),
            Promocion(
                id = 6,
                nombre = "Restaurante Sushi",
                imagenUrl = "https://picsum.photos/200/300?random=6",
                descuento = "20% OFF",
                categoria = "Comida",
                expiraEn = 10,
                ubicacion = "2.5 km",
                esFavorito = false,
                rating = 4.4,
                descripcion = "Sushi fresco con descuento especial"
            )
        )
    }

    private fun generarPromocionesExpiracionMock(): List<Promocion> {
        return listOf(
            Promocion(
                id = 7,
                nombre = "Gimnasio Fit",
                imagenUrl = "https://picsum.photos/200/300?random=7",
                descuento = "40% OFF",
                categoria = "Salud",
                expiraEn = 1,
                ubicacion = "0.8 km",
                esFavorito = false,
                rating = 4.6,
                descripcion = "Membresía mensual con acceso completo"
            ),
            Promocion(
                id = 8,
                nombre = "Taller Mecánico",
                imagenUrl = "https://picsum.photos/200/300?random=8",
                descuento = "15% OFF",
                categoria = "Servicios",
                expiraEn = 2,
                ubicacion = "1.2 km",
                esFavorito = false,
                rating = 4.2,
                descripcion = "Servicio de mantenimiento vehicular"
            )
        )
    }

    private fun generarPromocionesCercanasMock(): List<Promocion> {
        return listOf(
            Promocion(
                id = 9,
                nombre = "Cafetería Central",
                imagenUrl = "https://picsum.photos/200/300?random=9",
                descuento = "Café Gratis",
                categoria = "Comida",
                expiraEn = 7,
                ubicacion = "0.5 km",
                esFavorito = false,
                rating = 4.4,
                descripcion = "Café gratis con cualquier compra"
            ),
            Promocion(
                id = 10,
                nombre = "Farmacia 24/7",
                imagenUrl = "https://picsum.photos/200/300?random=10",
                descuento = "10% OFF",
                categoria = "Salud",
                expiraEn = 14,
                ubicacion = "0.3 km",
                esFavorito = false,
                rating = 4.1,
                descripcion = "Descuento en productos de farmacia"
            ),
            Promocion(
                id = 11,
                nombre = "Lavandería Express",
                imagenUrl = "https://picsum.photos/200/300?random=11",
                descuento = "2x1",
                categoria = "Servicios",
                expiraEn = 21,
                ubicacion = "0.7 km",
                esFavorito = false,
                rating = 4.3,
                descripcion = "Servicio de lavandería express"
            )
        )
    }
}