package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Objeto (Singleton) que gestiona la comunicación con un servicio remoto
 * (posiblemente una Cloud Function) para obtener los datos
 * de un negocio (Establecimiento) específico.
 *
 * Utiliza [HttpURLConnection] directamente para realizar la solicitud GET.
 */
object ServicioRemotoObtenerDatosNegocio {

    /**
     * Realiza una solicitud GET a un endpoint específico para obtener
     * los detalles completos de un negocio (Establecimiento) usando su ID.
     *
     * Esta operación se ejecuta en el dispatcher [Dispatchers.IO].
     *
     * @param idNegocio El identificador único del negocio (establecimiento)
     * que se desea consultar.
     * @return Un objeto [Negocio] poblado con los datos obtenidos
     * si la solicitud HTTP es 200 (OK) y el parseo JSON es exitoso.
     * Retorna `null` si la respuesta HTTP no es 200, si ocurre una
     * excepción de red, o si el JSON de respuesta no contiene el objeto "data".
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
                    foto = data.optString("foto")
                )
            } catch (e: Exception) {
                println("❌ Error al obtener datos del negocio: ${e.localizedMessage}")
                null
            }
        }
    }
}