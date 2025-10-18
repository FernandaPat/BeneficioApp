// mx.mfpp.beneficioapp.model.PromocionJovenAPI
package mx.mfpp.beneficioapp.model

import retrofit2.http.GET
import retrofit2.http.Query

interface PromocionJovenAPI {
    @GET("promociones")
    suspend fun obtenerPromociones(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("establecimiento_id") establecimientoId: Int? = null,
        @Query("estado") estado: String? = null
    ): PromocionesJovenResponse
}