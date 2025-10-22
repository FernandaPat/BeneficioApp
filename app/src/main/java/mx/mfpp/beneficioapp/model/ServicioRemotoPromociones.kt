package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 📡 Interfaz de la API
interface ApiPromociones {
    @GET("/")
    suspend fun getPromocionesPorNegocio(
        @Query("establecimiento_id") establecimientoId: Int
    ): Response<PromocionesResponse>
}


object ServicioRemotoPromociones {
    private const val BASE_URL = "https://listar-promociones-819994103285.us-central1.run.app/"

    val api: ApiPromociones by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiPromociones::class.java)
    }
}

