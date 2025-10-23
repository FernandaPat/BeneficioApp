/**
 * Archivo: ServicioRemotoObtenerDatosNegocio.kt
 *
 * Define un servicio remoto encargado de obtener la información detallada
 * de un negocio o establecimiento específico desde el servidor.
 *
 * Utiliza `HttpURLConnection` y corrutinas con `Dispatchers.IO` para realizar
 * la operación de forma asíncrona y eficiente.
 */
package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
/**
 * Objeto singleton responsable de la comunicación con el servicio remoto
 * que devuelve la información de un negocio específico.
 *
 * Realiza solicitudes HTTP GET hacia el endpoint remoto de Google Cloud Run,
 * maneja errores y convierte la respuesta JSON en un objeto [Negocio].
 */
object ServicioRemotoObtenerDatosNegocio {
    /**
     * Obtiene los datos completos de un negocio según su identificador.
     *
     * Realiza una solicitud HTTP GET al endpoint del servidor y construye un objeto [Negocio]
     * a partir de la respuesta JSON. En caso de error o respuesta inválida,
     * devuelve `null`.
     *
     * @param idNegocio Identificador único del establecimiento que se desea consultar.
     * @return Objeto [Negocio] con la información obtenida del servidor o `null` si ocurre un error.
     */
    suspend fun obtenerDatosNegocio(idNegocio: Int): Negocio? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://obtener-datos-negocio-819994103285.us-central1.run.app/?id_establecimiento=$idNegocio")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    println("❌ Error HTTP $responseCode")
                    return@withContext null
                }

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("🟣 RESPUESTA API NEGOCIO → $response")

                val json = JSONObject(response)
                val data = json.optJSONObject("data") ?: return@withContext null

                return@withContext Negocio(
                    id = idNegocio,
                    nombre = data.optString("nombre"),
                    correo = data.optString("correo"),
                    telefono = data.optString("numero_de_telefono"),
                    direccion = data.optString("direccion"),
                    categoria = data.optString("categoria"),
                    foto = data.optString("foto") // 👈 IMPORTANTE: foto del backend
                )
            } catch (e: Exception) {
                println("❌ Error al obtener datos del negocio: ${e.localizedMessage}")
                null
            }
        }
    }
}
