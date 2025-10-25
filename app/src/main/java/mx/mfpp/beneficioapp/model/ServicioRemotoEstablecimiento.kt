package mx.mfpp.beneficioapp.mode


import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.mfpp.beneficioapp.model.Establecimiento
import mx.mfpp.beneficioapp.model.SessionManager
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Objeto (Singleton) que gestiona la comunicación con un servicio remoto
 * (posiblemente una Cloud Function) para obtener la lista de establecimientos.
 *
 * Utiliza [HttpURLConnection] directamente para realizar la solicitud GET.
 */
object ServicioRemotoEstablecimiento {

    /**
     * URL base del servicio (Cloud Function) que devuelve la
     * lista de establecimientos.
     */
    private const val BASE_URL =
        "https://lista-establecimiento-819994103285.us-central1.run.app/establecimientos"

    /**
     * Obtiene la lista de establecimientos desde el servicio remoto.
     *
     * Esta operación se ejecuta en el dispatcher [Dispatchers.IO].
     *
     * Intenta obtener el `idUsuario` desde [SessionManager] (si se provee un [Context])
     * para adjuntarlo como parámetro query (`id_usuario`) en la URL.
     * Si el contexto es nulo o falla la obtención, se usa `id_usuario=0`.
     *
     * Parsea la respuesta JSON y la convierte en una lista de [Establecimiento].
     *
     * @param context El [Context] (opcional) de la aplicación, usado para
     * acceder a [SessionManager] y obtener el ID de usuario.
     * @return Una [List] de [Establecimiento]. Retorna una lista vacía si
     * la solicitud falla (error HTTP, excepción de red, o error de parseo).
     */
    suspend fun obtenerEstablecimientos(context: Context? = null): List<Establecimiento> =
        withContext(Dispatchers.IO) {
            val idUsuario = try {
                context?.let { SessionManager(it).getJovenId() } ?: 0
            } catch (e: Exception) {
                Log.e("SERVICIO_EST", "Error obteniendo idUsuario: ${e.message}")
                0
            }

            val urlString = "$BASE_URL?id_usuario=$idUsuario"
            Log.d("SERVICIO_EST", "🌐 GET $urlString")

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection

            return@withContext try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val responseText =
                        connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("SERVICIO_EST", "✅ Respuesta completa: $responseText")

                    val json = JSONObject(responseText)
                    val dataArray = json.getJSONArray("data")

                    val lista = (0 until dataArray.length()).map { i ->
                        val item = dataArray.getJSONObject(i)

                        // Extraer nombre de categoría (maneja diferentes estructuras del backend)
                        val categoriaObj = item.optJSONObject("categoria")
                        val nombreCategoria = when {
                            categoriaObj != null -> categoriaObj.optString("nombre", "")
                            item.has("nombre_categoria") -> item.optString("nombre_categoria", "")
                            item.has("categoria_nombre") -> item.optString("categoria_nombre", "")
                            else -> ""
                        }

                        val est = Establecimiento(
                            id_establecimiento = item.optInt("id_establecimiento"),
                            nombre = item.optString("nombre", ""),
                            colonia = item.optString("colonia", ""),
                            nombre_categoria = nombreCategoria,
                            direccion = item.optString("direccion", ""),
                            telefono = item.optString("telefono", ""),
                            latitud = item.optDouble("latitud", 0.0),
                            longitud = item.optDouble("longitud", 0.0),
                            imagen = item.optString("foto", ""),
                            es_favorito = item.optBoolean("es_favorito", false)
                        )

                        Log.d(
                            "SERVICIO_EST",
                            "🏪 ${est.nombre} | 🏷️ ${est.nombre_categoria} | 📍 ${est.colonia} | ❤️ ${est.es_favorito}"
                        )

                        est
                    }

                    Log.d("SERVICIO_EST", "📦 Total establecimientos recibidos: ${lista.size}")
                    lista
                } else {
                    Log.e("SERVICIO_EST", "❌ Error HTTP $responseCode")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("SERVICIO_EST", "❌ Error de red: ${e.message}")
                emptyList()
            } finally {
                connection.disconnect()
            }
        }
}