package mx.mfpp.beneficioapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.network.PromocionesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import kotlinx.coroutines.CoroutineScope

class PromocionesViewModel : ViewModel() {
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
