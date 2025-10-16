package mx.mfpp.beneficioapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.network.PromocionesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PromocionesViewModel : ViewModel() {

    // comentario epico
    private val _favoritos = MutableStateFlow<List<Promocion>>(emptyList())
    val favoritos: StateFlow<List<Promocion>> = _favoritos.asStateFlow()

    private val _nuevasPromociones = MutableStateFlow<List<Promocion>>(emptyList())
    val nuevasPromociones: StateFlow<List<Promocion>> = _nuevasPromociones.asStateFlow()

    private val _promocionesExpiracion = MutableStateFlow<List<Promocion>>(emptyList())
    val promocionesExpiracion: StateFlow<List<Promocion>> = _promocionesExpiracion.asStateFlow()

    private val _promocionesCercanas = MutableStateFlow<List<Promocion>>(emptyList())
    val promocionesCercanas: StateFlow<List<Promocion>> = _promocionesCercanas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarPromociones()
    }

    fun cargarPromociones() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(800)
                _favoritos.value = generarFavoritosMock()
                _nuevasPromociones.value = generarNuevasPromocionesMock()
                _promocionesExpiracion.value = generarPromocionesExpiracionMock()
                _promocionesCercanas.value = generarPromocionesCercanasMock()
            } catch (e: Exception) {
                _error.value = "Error al cargar promociones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refrescarPromociones() {
        cargarPromociones()
    }

    fun clearError() {
        _error.value = null
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

    //Fin del comentario epico
    val promos = mutableStateListOf(
        Promocion(1, "Ropa 10% de descuento", "https://picsum.photos/seed/1/300", "10%", "Comida", 3, "Atizapán", false, 4.5, "Cupon ropa 10%"),
        Promocion(2, "Promoción 2", "https://picsum.photos/seed/2/300", "15%", "Entretenimiento", 0, "CDMX", true, 3.8, "Descripción"),
        Promocion(3, "Promoción 3", null, "20%", "Cine", 5, "CDMX", false, null, "Descripción")
    )

    private val api: PromocionesApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://9somwbyil5.execute-api.us-east-1.amazonaws.com/prod/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PromocionesApi::class.java)
    }

    // Lógica para eliminar promoción
    fun eliminarPromocion(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.eliminarPromocion(id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) { // Aquí es donde se usa isSuccessful correctamente
                        val promo = promos.firstOrNull { it.id == id }
                        promo?.let { promos.remove(it) }
                    } else {
                        Log.e("EliminarPromocion", "Error al eliminar la promoción: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("EliminarPromocion", "Error al hacer la solicitud: ${e.message}")
            }
        }
    }
}
