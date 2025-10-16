package mx.mfpp.beneficioapp.network

import retrofit2.http.DELETE
import retrofit2.Response
import retrofit2.http.Path

interface PromocionesApi {
    @DELETE("deletePromocion/{id}")
    suspend fun eliminarPromocion(@Path("id") id: Int): Response<Unit> // Respuesta vacía si no es necesario retornar nada
}
