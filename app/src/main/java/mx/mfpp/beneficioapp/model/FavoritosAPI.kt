package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de Retrofit que define los endpoints para gestionar
 * los establecimientos favoritos de un usuario.
 */
interface FavoritosAPI {

    /**
     * Agrega un establecimiento a la lista de favoritos de un usuario.
     *
     * @param request El cuerpo de la solicitud [FavoritoRequest] que contiene
     * el ID del usuario y el ID del establecimiento.
     * @return Una [Response] de Retrofit que envuelve un [FavoritoResponse].
     */
    @POST("getFavoritos")
    suspend fun agregarFavorito(
        @Body request: FavoritoRequest
    ): Response<FavoritoResponse>

    /**
     * Elimina un establecimiento de la lista de favoritos de un usuario.
     *
     * @param request El cuerpo de la solicitud [FavoritoRequest] que contiene
     * el ID del usuario y el ID del establecimiento a eliminar.
     * @return Una [Response] de Retrofit que envuelve un [FavoritoResponse].
     */
    @HTTP(method = "DELETE", path = "getFavoritos", hasBody = true)
    suspend fun eliminarFavorito(
        @Body request: FavoritoRequest
    ): Response<FavoritoResponse>

    /**
     * Obtiene la lista completa de establecimientos favoritos para un usuario específico.
     *
     * @param idUsuario El identificador único del usuario cuyos favoritos se desean obtener.
     * @return Una [Response] de Retrofit que envuelve un [FavoritosListResponse].
     */
    @GET("getFavoritos")
    suspend fun obtenerFavoritos(
        @Query("id_usuario") idUsuario: Int
    ): Response<FavoritosListResponse>
}

/**
 * Modelo de datos para la solicitud (request) de agregar o eliminar un favorito.
 *
 * @property id_usuario El ID del usuario que realiza la acción.
 * @property id_establecimiento El ID del establecimiento a agregar o eliminar.
 */
data class FavoritoRequest(
    val id_usuario: Int,
    val id_establecimiento: Int
)

/**
 * Modelo de datos para la respuesta (response) al agregar o eliminar un favorito.
 *
 * @property message Mensaje descriptivo del resultado (ej. "Favorito agregado").
 * @property id_favorito El ID único del registro de favorito creado (opcional).
 * @property id_usuario El ID del usuario asociado a la acción.
 * @property id_establecimiento El ID del establecimiento asociado a la acción.
 */
data class FavoritoResponse(
    val message: String,
    val id_favorito: Int? = null,
    val id_usuario: Int,
    val id_establecimiento: Int
)

/**
 * Modelo de datos para la respuesta (response) que contiene la lista de favoritos.
 *
 * @property favoritos La lista de [FavoritoDetalle] del usuario.
 * @property total El conteo total de favoritos encontrados.
 */
data class FavoritosListResponse(
    val favoritos: List<FavoritoDetalle>,
    val total: Int
)

/**
 * Modelo de datos que representa los detalles de un solo establecimiento
 * marcado como favorito.
 *
 * @property id_favoritos El identificador único del registro "favorito".
 * @property id_establecimiento El identificador único del establecimiento.
 * @property nombre_establecimiento El nombre comercial del establecimiento.
 * @property foto La URL de la imagen del establecimiento (puede ser nula).
 * @property colonia La colonia donde se ubica el establecimiento (puede ser nula).
 * @property nombre_categoria El nombre de la categoría del establecimiento (puede ser nulo).
 */
data class FavoritoDetalle(
    val id_favoritos: Int,
    val id_establecimiento: Int,
    val nombre_establecimiento: String,
    val foto: String?,
    val colonia: String?,
    val nombre_categoria: String?
)