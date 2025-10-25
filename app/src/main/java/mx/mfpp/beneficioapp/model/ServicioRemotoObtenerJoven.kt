package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Objeto (Singleton) que gestiona la comunicación con un servicio remoto
 * (posiblemente una Cloud Function) para obtener los datos
 * de un usuario (Joven) específico.
 *
 * Utiliza [HttpURLConnection] directamente para realizar la solicitud GET.
 */
object ServicioRemotoObtenerDatosJoven {

    /**
     * Realiza una solicitud GET a un endpoint específico para obtener
     * los detalles completos de un usuario (Joven) usando su ID.
     *
     * Esta operación se ejecuta en el dispatcher [Dispatchers.IO].
     *
     * @param idUsuario El identificador único del usuario (joven)
     * que se desea consultar.
     * @return Un objeto [Joven] poblado con los datos obtenidos
     * si la solicitud HTTP es 200 (OK) y el parseo JSON es exitoso.
     * Retorna `null` si la respuesta HTTP no es 200, si ocurre una
     * excepción de red, o si el JSON de respuesta no contiene el objeto "data".
     */
    suspend fun obtenerDatosJoven(idUsuario: Int): Joven? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://obtener-datos-joven-819994103285.us-central1.run.app/?id_usuario=$idUsuario")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    println("❌ Error HTTP $responseCode")
                    return@withContext null
                }

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("🟣 RESPUESTA API → $response")

                val json = JSONObject(response)
                val data = json.optJSONObject("data") ?: return@withContext null

                // Mapea los campos del JSON al objeto Joven,
                // manejando campos alternativos (ej. correo_electronico o email)
                return@withContext Joven(
                    id = data.optInt("id"),
                    nombre = data.optString("nombre"),
                    correo = data.optString("correo_electronico", data.optString("email", "")),
                    telefono = data.optString("telefono", ""),
                    direccion = if (data.has("direccion")) data.optString("direccion") else "",
                    foto = data.optString("foto", "")
                )

            } catch (e: Exception) {
                println("❌ Error al obtener datos del joven: ${e.localizedMessage}")
                null
            }
        }
    }
}