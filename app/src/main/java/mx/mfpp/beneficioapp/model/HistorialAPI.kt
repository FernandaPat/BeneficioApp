package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HistorialAPI {

    @GET("historial-canjes/{idUsuario}")
    suspend fun obtenerHistorialUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<HistorialJovenResponse>
}
