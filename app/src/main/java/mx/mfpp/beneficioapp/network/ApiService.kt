package mx.mfpp.beneficioapp.network

import mx.mfpp.beneficioapp.model.CrearCuentaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("registerJoven")
    suspend fun registrarUsuario(@Body request: CrearCuentaRequest): Response<Void>

}
