/**
 * Archivo: ServicioRemotoFavoritos.kt
 *
 * Define un servicio remoto encargado de gestionar las operaciones
 * relacionadas con los favoritos del usuario dentro de la aplicación.
 *
 * Incluye métodos para agregar, eliminar y obtener establecimientos marcados como favoritos.
 * Utiliza Retrofit con Gson para la comunicación con la API alojada en AWS API Gateway.
 */
package mx.mfpp.beneficioapp.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Objeto singleton que administra las operaciones de red relacionadas con los favoritos.
 *
 * Crea y expone una instancia de Retrofit configurada para conectarse con la API de favoritos.
 */
object ServicioRemotoFavoritos {
    private const val URL_BASE = "https://rs2xlkq5el.execute-api.us-east-1.amazonaws.com/default/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicio: FavoritosAPI by lazy {
        retrofit.create(FavoritosAPI::class.java)
    }
    /**
     * Agrega un establecimiento a la lista de favoritos del usuario.
     *
     * @param idUsuario Identificador del usuario que agrega el favorito
     * @param idEstablecimiento Identificador del establecimiento a marcar como favorito
     * @return Resultado exitoso con mensaje de confirmación o fallo con la excepción correspondiente
     */
    suspend fun agregarFavorito(idUsuario: Int, idEstablecimiento: Int): Result<String> {
        return try {
            val request = FavoritoRequest(idUsuario, idEstablecimiento)
            val response = servicio.agregarFavorito(request)

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("FAVORITOS_SERVICIO", "✅ ${body?.message}")
                Result.success(body?.message ?: "Agregado a favoritos")
            } else if (response.code() == 409) {
                // 🔹 CAMBIO: Manejar 409 como caso especial
                Log.w("FAVORITOS_SERVICIO", "⚠️ Ya está en favoritos")
                Result.failure(Exception("Este establecimiento ya está en favoritos"))
            } else {
                val errorMsg = "Error ${response.code()}: ${response.message()}"
                Log.e("FAVORITOS_SERVICIO", "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("FAVORITOS_SERVICIO", "❌ Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
    /**
     * Elimina un establecimiento de la lista de favoritos del usuario.
     *
     * @param idUsuario Identificador del usuario que elimina el favorito
     * @param idEstablecimiento Identificador del establecimiento a eliminar de favoritos
     * @return Resultado exitoso con mensaje de confirmación o fallo con la excepción correspondiente
     */
    suspend fun eliminarFavorito(idUsuario: Int, idEstablecimiento: Int): Result<String> {
        return try {
            val request = FavoritoRequest(idUsuario, idEstablecimiento)
            val response = servicio.eliminarFavorito(request)

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("FAVORITOS_SERVICIO", "✅ ${body?.message}")
                Result.success(body?.message ?: "Eliminado de favoritos")
            } else {
                val errorMsg = "Error ${response.code()}: ${response.message()}"
                Log.e("FAVORITOS_SERVICIO", "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("FAVORITOS_SERVICIO", "❌ Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
    /**
     * Obtiene la lista de establecimientos marcados como favoritos por el usuario.
     *
     * @param idUsuario Identificador del usuario del cual se obtendrán los favoritos
     * @return Resultado exitoso con una lista de [FavoritoDetalle] o fallo con la excepción correspondiente
     */
    suspend fun obtenerFavoritos(idUsuario: Int): Result<List<FavoritoDetalle>> {
        return try {
            val response = servicio.obtenerFavoritos(idUsuario)

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("FAVORITOS_SERVICIO", "✅ ${body?.total} favoritos obtenidos")
                Result.success(body?.favoritos ?: emptyList())
            } else {
                val errorMsg = "Error ${response.code()}: ${response.message()}"
                Log.e("FAVORITOS_SERVICIO", "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("FAVORITOS_SERVICIO", "❌ Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}