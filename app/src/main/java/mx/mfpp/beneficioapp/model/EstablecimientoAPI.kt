package mx.mfpp.beneficioapp.model

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de Retrofit para definir los endpoints relacionados con los establecimientos.
 */
interface EstablecimientoAPI {

    /**
     * Obtiene una lista paginada de establecimientos desde el servidor.
     *
     * @param page El número de página que se desea obtener (default: 1).
     * @param limit La cantidad de establecimientos por página (default: 50).
     * @param idUsuario El ID del usuario (opcional) para filtrar o personalizar la respuesta.
     * @return Un objeto [EstablecimientosResponse] que contiene la lista de establecimientos.
     */
    @GET("establecimientos")
    suspend fun obtenerEstablecimientos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("id_usuario") idUsuario: Int? = null
    ): EstablecimientosResponse
}