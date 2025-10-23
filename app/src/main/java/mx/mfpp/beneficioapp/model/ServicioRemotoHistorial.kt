package mx.mfpp.beneficioapp.model
/**
 * Archivo: ServicioRemotoHistorial.kt
 *
 * Define un servicio remoto encargado de obtener el historial de promociones canjeadas
 * por un usuario joven desde el servidor.
 *
 * Utiliza Retrofit con conversión JSON mediante Gson y maneja las solicitudes de forma
 * asíncrona con corrutinas.
 */
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Objeto singleton responsable de la comunicación con el servicio remoto
 * que proporciona el historial de canjes del usuario.
 *
 * Implementa la lógica para ejecutar la solicitud HTTP y manejar las respuestas,
 * incluyendo logs de diagnóstico y control de errores.
 */
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
    /**
     * Obtiene el historial completo de promociones canjeadas por un usuario joven.
     *
     * Realiza una solicitud HTTP GET al endpoint remoto correspondiente y devuelve
     * una lista con los registros obtenidos, en caso de éxito.
     *
     * @param idUsuario Identificador del usuario joven cuyo historial se desea consultar
     * @return Lista de objetos [HistorialPromocionUsuario] con los canjes registrados;
     * devuelve una lista vacía si ocurre un error o si no hay registros disponibles
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