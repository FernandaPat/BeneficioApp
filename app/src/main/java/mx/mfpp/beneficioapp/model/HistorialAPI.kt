package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HistorialAPI {

    @GET("historialUsuario/{idUsuario}")
    suspend fun obtenerHistorialUsuario(
        @Path("idUsuario") idUsuario: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<HistorialResponse>

    // Opcional: Si quieres obtener historial por establecimiento también
    @GET("historialEstablecimiento/{idEstablecimiento}")
    suspend fun obtenerHistorialEstablecimiento(
        @Path("idEstablecimiento") idEstablecimiento: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<HistorialResponse>
}