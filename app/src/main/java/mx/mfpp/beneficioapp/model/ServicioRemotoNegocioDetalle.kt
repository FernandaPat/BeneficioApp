package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicioRemotoNegocioDetalle {
    private const val URL_BASE = "https://listar-promociones-819994103285.us-central1.run.app/"

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
            val resp = api.obtenerPromociones(establecimientoId = establecimientoId, estado = "activa")
            resp.data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
