/**
 * Archivo: HistorialAPI.kt
 *
 * Define la interfaz de comunicación con el endpoint de historial de canjes
 * mediante Retrofit.
 *
 * Permite obtener el historial de promociones canjeadas por un usuario joven.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
/**
 * Interfaz que define las operaciones HTTP relacionadas con el historial de canjes.
 *
 * Utiliza Retrofit para realizar solicitudes GET al recurso "historial-canjes".
 */
interface HistorialAPI {
    /**
     * Obtiene el historial de canjes de un usuario joven específico.
     *
     * @param idUsuario Identificador del usuario del cual se desea obtener el historial
     * @return Respuesta HTTP con un objeto [HistorialJovenResponse] que contiene los registros del historial
     */
    @GET("historial-canjes/{idUsuario}")
    suspend fun obtenerHistorialUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<HistorialJovenResponse>
}
