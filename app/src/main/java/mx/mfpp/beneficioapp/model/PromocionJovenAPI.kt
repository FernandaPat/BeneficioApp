package mx.mfpp.beneficioapp.model

import retrofit2.http.GET

interface PromocionJovenAPI {
    @GET("promociones")
    suspend fun obtenerPromociones(): PromocionesJovenResponse
}