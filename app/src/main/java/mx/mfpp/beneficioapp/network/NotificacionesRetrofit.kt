package mx.mfpp.beneficioapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NotificacionesRetrofit {
    private const val BASE_URL = "https://registrar-token-819994103285.us-central1.run.app"

    val api: NotificacionesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificacionesApi::class.java)
    }
}