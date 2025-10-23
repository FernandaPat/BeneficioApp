package mx.mfpp.beneficioapp.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicioRemotoHistorial {

    private const val URL_BASE = "https://historial-canjes-joven-819994103285.us-central1.run.app/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicio: HistorialAPI by lazy {
        retrofit.create(HistorialAPI::class.java)
    }

    suspend fun obtenerHistorialUsuario(idUsuario: Int): List<HistorialPromocionUsuario> {
        return try {
            Log.d("HISTORIAL_SERVICIO", "📖 Obteniendo historial para usuario: $idUsuario")
            val response = servicio.obtenerHistorialUsuario(idUsuario)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d("HISTORIAL_SERVICIO", "✅ Historial obtenido: ${body.data.size} elementos")
                    body.data
                } else {
                    Log.e("HISTORIAL_SERVICIO", "❌ Respuesta no exitosa: ${body?.success}")
                    emptyList()
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("HISTORIAL_SERVICIO", "❌ Error HTTP ${response.code()}: $errorBody")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HISTORIAL_SERVICIO", "❌ Excepción: ${e.message}", e)
            emptyList()
        }
    }
}