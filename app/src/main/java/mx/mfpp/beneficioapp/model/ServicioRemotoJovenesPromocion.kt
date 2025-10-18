// mx.mfpp.beneficioapp.model.ServicioRemotoJovenesPromocion
package mx.mfpp.beneficioapp.model

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicioRemotoJovenesPromocion {
    private const val URL_BASE = "https://9somwbyil5.execute-api.us-east-1.amazonaws.com/prod/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicio by lazy {
        retrofit.create(PromocionJovenAPI::class.java)
    }

    suspend fun obtenerPromociones(): List<PromocionJoven> {
        try {
            val response = servicio.obtenerPromociones()
            return response.data
        } catch (e: HttpException) {
            println("Error HTTP, código: ${e.code()}")
            throw e
        } catch (e: Exception) {
            println("Error de conexión: ${e.message}")
            throw e
        }
    }
}