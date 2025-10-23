/**
 * Archivo: EstablecimientoAPI.kt
 *
 * Define la interfaz de comunicación con el endpoint de establecimientos
 * en el servidor remoto mediante Retrofit.
 *
 * Proporciona los métodos necesarios para obtener la lista de establecimientos
 * con soporte para paginación y filtrado por usuario.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.http.GET
import retrofit2.http.Query
/**
 * Interfaz que define las operaciones HTTP relacionadas con establecimientos.
 *
 * Utiliza Retrofit para realizar solicitudes de tipo GET al recurso "establecimientos".
 */
interface EstablecimientoAPI {
    @GET("establecimientos")
    suspend fun obtenerEstablecimientos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("id_usuario") idUsuario: Int? = null
    ): EstablecimientosResponse
}