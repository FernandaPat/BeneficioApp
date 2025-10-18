package mx.mfpp.beneficioapp.network

import mx.mfpp.beneficioapp.model.CrearCuentaRequest
import mx.mfpp.beneficioapp.model.PromocionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("registerJoven")
    suspend fun registrarUsuario(@Body request: CrearCuentaRequest): Response<Void>

    @Headers("Content-Type: application/json")
    @POST("registroPromocion")
    suspend fun registrarPromocion(
        @Body promocion: PromocionRequest
    ): Response<Unit>
}
