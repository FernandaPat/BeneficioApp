package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz de Retrofit que define los endpoints para consultar
 * el historial de canjes.
 */
interface HistorialAPI {

    /**
     * Obtiene el historial de canjes de promociones para un usuario específico.
     *
     * @param idUsuario El identificador único del usuario (joven) cuyo historial se desea consultar.
     * @return Una [Response] de Retrofit que envuelve un [HistorialJovenResponse]
     * con la lista de canjes.
     */
    @GET("historial-canjes/{idUsuario}")
    suspend fun obtenerHistorialUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<HistorialJovenResponse>
}