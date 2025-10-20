package mx.mfpp.beneficioapp.network

import mx.mfpp.beneficioapp.model.CrearCuentaRequest
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.AgregarPromocionRequest
import mx.mfpp.beneficioapp.model.AgregarPromocionesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("registerJoven")
    suspend fun registrarUsuario(@Body request: CrearCuentaRequest): Response<Void>

    @Headers("Content-Type: application/json")
    @POST("registroPromocion")
    suspend fun registrarPromocion(
        @Body promocion: AgregarPromocionRequest
    ): Response<Unit>

    @GET("promociones")
    suspend fun obtenerPromociones(
        @Query("establecimiento_id") establecimientoId: Int
    ): AgregarPromocionesResponse
    @DELETE("deletePromocion/{id}")
    suspend fun eliminarPromocion(@Path("id") id: Int): retrofit2.Response<Unit>


    @GET("promocion/{id}")
    suspend fun obtenerPromocionPorId(
        @Path("id") id: Int
    ): Promocion

    @PUT("promocion/{id}")
    suspend fun actualizarPromocion(
        @Path("id") id: Int,
        @Body promocion: Promocion
    ): Response<Unit>


}
