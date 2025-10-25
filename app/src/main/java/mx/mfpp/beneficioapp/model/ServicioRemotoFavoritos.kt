// mx.mfpp.beneficioapp.model.ServicioRemotoFavoritos
package mx.mfpp.beneficioapp.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto (Singleton) que gestiona la comunicación con la API de Favoritos.
 *
 * Proporciona una instancia de Retrofit y encapsula las llamadas a [FavoritosAPI]
 * (agregar, eliminar, obtener), manejando las respuestas y errores
 * en un objeto [Result].
 */
object ServicioRemotoFavoritos {
    /** URL base del endpoint de AWS API Gateway para el servicio de Favoritos. */
    private const val URL_BASE = "https://rs2xlkq5el.execute-api.us-east-1.amazonaws.com/default/"

    /**
     * Instancia [Retrofit] configurada con la [URL_BASE] y [GsonConverterFactory].
     * Se inicializa de forma perezosa (lazy).
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Instancia de la [FavoritosAPI] (el servicio de Retrofit) creada a partir de [retrofit].
     * Se inicializa de forma perezosa (lazy).
     */
    private val servicio: FavoritosAPI by lazy {
        retrofit.create(FavoritosAPI::class.java)
    }

    /**
     * Intenta agregar un establecimiento a la lista de favoritos de un usuario.
     *
     * @param idUsuario El ID del usuario que agrega el favorito.
     * @param idEstablecimiento El ID del establecimiento a agregar.
     * @return [Result.success] con un mensaje (ej. "Agregado a favoritos") si la
     * operación fue exitosa (HTTP 2xx).
     * [Result.failure] con una [Exception] si ocurre un error (HTTP 4xx/5xx)
     * o una excepción de red. Maneja específicamente el código 409 (Conflicto)
     * si el favorito ya existe.
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
     * Intenta eliminar un establecimiento de la lista de favoritos de un usuario.
     *
     * @param idUsuario El ID del usuario que elimina el favorito.
     * @param idEstablecimiento El ID del establecimiento a eliminar.
     * @return [Result.success] con un mensaje (ej. "Eliminado de favoritos") si la
     * operación fue exitosa (HTTP 2xx).
     * [Result.failure] con una [Exception] si ocurre un error (HTTP 4xx/5xx)
     * o una excepción de red.
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
     * Obtiene la lista completa de establecimientos favoritos para un usuario específico.
     *
     * @param idUsuario El ID del usuario cuyos favoritos se desean obtener.
     * @return [Result.success] con la [List] de [FavoritoDetalle].
     * Retorna una lista vacía si la respuesta es exitosa pero no hay favoritos.
     * [Result.failure] con una [Exception] si ocurre un error (HTTP 4xx/5xx)
     * o una excepción de red.
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