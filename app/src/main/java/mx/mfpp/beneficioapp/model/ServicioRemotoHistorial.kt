package mx.mfpp.beneficioapp.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto (Singleton) que gestiona la comunicación con la API remota
 * del historial de canjes del usuario (joven).
 *
 * Encapsula la lógica de Retrofit y el manejo de respuestas para [HistorialAPI].
 */
object ServicioRemotoHistorial {

    /** URL base del servicio (Cloud Function) para obtener el historial. */
    private const val URL_BASE = "https://historial-canjes-joven-819994103285.us-central1.run.app/"

    /**
     * Instancia [Retrofit] configurada con [URL_BASE] y [GsonConverterFactory].
     * Se inicializa de forma perezosa (lazy).
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Instancia de la [HistorialAPI] (el servicio de Retrofit) creada a partir de [retrofit].
     * Se inicializa de forma perezosa (lazy).
     */
    private val servicio: HistorialAPI by lazy {
        retrofit.create(HistorialAPI::class.java)
    }

    /**
     * Obtiene la lista de promociones canjeadas (historial) para un usuario específico.
     *
     * Realiza una llamada de red a través de [servicio].
     * Maneja las respuestas exitosas (HTTP 200 y `success: true`) y los errores
     * (HTTP no exitoso o excepciones), registrando los resultados en Logcat
     * con la etiqueta "HISTORIAL_SERVICIO".
     *
     * @param idUsuario El identificador único del usuario (joven) cuyo historial se desea consultar.
     * @return Una [List] de [HistorialPromocionUsuario]. Retorna una lista vacía si
     * la solicitud falla, la respuesta no es exitosa (ej. `success: false`),
     * o si ocurre cualquier excepción.
     */
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