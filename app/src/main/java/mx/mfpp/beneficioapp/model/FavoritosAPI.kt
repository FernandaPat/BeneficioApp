// mx.mfpp.beneficioapp.model.FavoritosAPI
package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.*

interface FavoritosAPI {

    @POST("favoritos")
    suspend fun agregarFavorito(
        @Body request: FavoritoRequest
    ): Response<FavoritoResponse>

    @HTTP(method = "DELETE", path = "favoritos", hasBody = true)
    suspend fun eliminarFavorito(
        @Body request: FavoritoRequest
    ): Response<FavoritoResponse>

    @GET("favoritos")
    suspend fun obtenerFavoritos(
        @Query("id_usuario") idUsuario: Int
    ): Response<FavoritosListResponse>
}

// Request body para agregar/eliminar
data class FavoritoRequest(
    val id_usuario: Int,
    val id_establecimiento: Int
)

// Respuesta de agregar/eliminar
data class FavoritoResponse(
    val message: String,
    val id_favorito: Int? = null,
    val id_usuario: Int,
    val id_establecimiento: Int
)

// Respuesta de listar favoritos
data class FavoritosListResponse(
    val favoritos: List<FavoritoDetalle>,
    val total: Int
)

data class FavoritoDetalle(
    val id_favoritos: Int,
    val id_establecimiento: Int,
    val nombre_establecimiento: String,
    val foto: String?,
    val colonia: String?,
    val nombre_categoria: String?
)