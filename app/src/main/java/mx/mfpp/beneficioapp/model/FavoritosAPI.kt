/**
 * Archivo: FavoritosAPI.kt
 *
 * Define la interfaz de comunicación con el endpoint de favoritos
 * en el servidor remoto mediante Retrofit.
 *
 * Permite agregar, eliminar y obtener establecimientos marcados como favoritos
 * por un usuario dentro de la aplicación.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.*
/**
 * Interfaz que define las operaciones HTTP relacionadas con la gestión de favoritos.
 *
 * Utiliza Retrofit para realizar solicitudes POST, DELETE y GET al recurso "getFavoritos".
 */
interface FavoritosAPI {

    /**
     * Agrega un establecimiento a la lista de favoritos de un usuario.
     *
     * @param request Cuerpo de la solicitud con los IDs del usuario y del establecimiento
     * @return Respuesta HTTP que contiene el resultado de la operación
     */
    @POST("getFavoritos")
    suspend fun agregarFavorito(
        @Body request: FavoritoRequest
    ): Response<FavoritoResponse>
    /**
     * Elimina un establecimiento de la lista de favoritos de un usuario.
     *
     * @param request Cuerpo de la solicitud con los IDs del usuario y del establecimiento
     * @return Respuesta HTTP que contiene el resultado de la operación
     */
    @HTTP(method = "DELETE", path = "getFavoritos", hasBody = true)
    suspend fun eliminarFavorito(
        @Body request: FavoritoRequest
    ): Response<FavoritoResponse>
    /**
     * Obtiene la lista completa de establecimientos favoritos de un usuario.
     *
     * @param idUsuario Identificador del usuario
     * @return Respuesta HTTP con una lista de favoritos y su información detallada
     */
    @GET("getFavoritos")
    suspend fun obtenerFavoritos(
        @Query("id_usuario") idUsuario: Int
    ): Response<FavoritosListResponse>
}