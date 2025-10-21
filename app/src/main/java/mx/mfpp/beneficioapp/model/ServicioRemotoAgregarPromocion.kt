package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiAgregarPromocion {
    @Headers("Content-Type: application/json")
    @POST("/")
    suspend fun agregarPromocion(@Body request: AgregarPromocionRequest): Response<Unit>
}

object ServicioRemotoAgregarPromocion {
    private const val BASE_URL = "https://agregar-promocion-819994103285.us-central1.run.app/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiAgregarPromocion by lazy {
        retrofit.create(ApiAgregarPromocion::class.java)
    }
}
