// mx.mfpp.beneficioapp.model.ServicioRemotoPromocion
package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicioRemotoNegocioDetalle {
    private const val URL_BASE = "https://9somwbyil5.execute-api.us-east-1.amazonaws.com/prod/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api: PromocionJovenAPI by lazy {
        retrofit.create(PromocionJovenAPI::class.java)
    }

    suspend fun obtenerPromocionesActivas(establecimientoId: Int): List<PromocionJoven> {
        return try {
            println("🔗 Obteniendo promociones para establecimiento: $establecimientoId")
            val resp = api.obtenerPromociones(establecimientoId = establecimientoId, estado = "activa")
            println("✅ Promociones obtenidas: ${resp.data.size}")
            resp.data
        } catch (e: Exception) {
            println("❌ Error obteniendo promociones: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}