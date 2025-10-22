package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.Response

interface ApiEliminarPromocion {
    @DELETE("eliminarPromocion/{id_promocion}")
    suspend fun eliminarPromocion(@Path("id_promocion") idPromocion: Int): Response<Unit>
}

object ServicioRemotoEliminarPromocion {
    private const val BASE_URL = "https://eliminar-promocion-819994103285.us-central1.run.app/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiEliminarPromocion by lazy {
        retrofit.create(ApiEliminarPromocion::class.java)
    }
}
