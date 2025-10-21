package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ServicioRemotoActualizarPromocion {

    @POST("https://actualizar-promocion-819994103285.us-central1.run.app/")
    suspend fun actualizarPromocion(
        @Body request: ActualizarPromocionRequest
    ): Response<Unit>

    companion object {
        val api: ServicioRemotoActualizarPromocion by lazy {
            Retrofit.Builder()
                .baseUrl("https://actualizar-promocion-819994103285.us-central1.run.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ServicioRemotoActualizarPromocion::class.java)
        }
    }
}
