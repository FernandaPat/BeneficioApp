// mx.mfpp.beneficioapp.model.EstablecimientoAPI
package mx.mfpp.beneficioapp.model

import retrofit2.http.GET
import retrofit2.http.Query

interface EstablecimientoAPI {
    @GET("establecimientos")
    suspend fun obtenerEstablecimientos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): EstablecimientosResponse
}