/* package mx.mfpp.beneficioapp.model

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ServicioRemoto {
    private const val URL_BASE = "https://api.jsonbin.io/v3/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicio by lazy {
        retrofit.create(PromocionAPI::class.java)
    }

    suspend fun obtenerCategorias(): List<Categoria> {
        return try {
            val respuesta = servicio.obtenerDatos()
            respuesta.record?.categorias ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerTodasPromociones(): List<Promocion> {
        return try {
            val respuesta = servicio.obtenerDatos()
            respuesta.record?.promociones ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun obtenerFavoritos(): List<Promocion> {
        return obtenerTodasPromociones().filter { it.esFavorito }
    }

    suspend fun obtenerNuevasPromociones(): List<Promocion> {
        return obtenerTodasPromociones()
            .filter { !it.esFavorito }
            .take(4)
    }

    suspend fun obtenerPromocionesPorExpiracion(): List<Promocion> {
        return obtenerTodasPromociones()
            .filter { (it.expiraEn ?: 999) <= 7 }
            .sortedBy { it.expiraEn }
            .take(3)
    }

    suspend fun obtenerPromocionesCercanas(): List<Promocion> {
        return obtenerTodasPromociones()
            .filter { it.ubicacion?.contains("km") == true }
            .filter {
                val distancia = it.ubicacion!!.replace(" km", "").toDoubleOrNull() ?: 999.0
                distancia < 5.0
            }
            .take(3)
    }
}
*/