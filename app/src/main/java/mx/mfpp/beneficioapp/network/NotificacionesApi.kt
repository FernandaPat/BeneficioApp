package mx.mfpp.beneficioapp.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificacionesApi {
    @POST("/")
    suspend fun registrarToken(
        @Body request: RegistrarTokenRequest
    ): Response<RegistrarTokenResponse>
}

data class RegistrarTokenRequest(
    val id_usuario: Int,
    val device_token: String,
    val plataforma: String = "android"
)

data class RegistrarTokenResponse(
    val message: String,
    val id_usuario: Int,
    val plataforma: String
)