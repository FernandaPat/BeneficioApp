// mx.mfpp.beneficioapp.model.ServicioRemotoFavoritos
package mx.mfpp.beneficioapp.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicioRemotoFavoritos {
    private const val URL_BASE = "https://9somwbyil5.execute-api.us-east-1.amazonaws.com/prod/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicio: FavoritosAPI by lazy {
        retrofit.create(FavoritosAPI::class.java)
    }

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