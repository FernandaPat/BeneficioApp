package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface ApiCrearCuenta {
    @POST("registroJoven") // 👈 o la ruta exacta que espera tu Cloud Run API
    suspend fun registrarUsuario(@Body request: CrearCuentaRequest): Response<Any>
}

object ServicioRemotoCrearCuenta {

    private const val BASE_URL = "https://registrojoven-819994103285.us-central1.run.app/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiCrearCuenta by lazy {
        retrofit.create(ApiCrearCuenta::class.java)
    }
}
