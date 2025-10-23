/**
 * Archivo: ServicioRemotoObtenerDatosJoven.kt
 *
 * Define un servicio remoto encargado de obtener la información personal
 * de un usuario joven desde el servidor.
 *
 * Utiliza `HttpURLConnection` para realizar la solicitud HTTP GET
 * y corrutinas con `Dispatchers.IO` para ejecutar la operación en un hilo de entrada/salida.
 */
package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
/**
 * Objeto singleton responsable de la comunicación con el servicio remoto
 * que devuelve la información detallada de un usuario joven.
 *
 * Gestiona la solicitud HTTP y convierte la respuesta JSON en un objeto [Joven],
 * manejando posibles errores de red y datos faltantes del backend.
 */
object ServicioRemotoObtenerDatosJoven {
    /**
     * Obtiene los datos personales y de contacto de un usuario joven.
     *
     * Realiza una solicitud HTTP GET al servidor, interpreta la respuesta JSON
     * y construye un objeto [Joven] con los datos obtenidos.
     *
     * @param idUsuario Identificador único del usuario joven cuyo perfil se desea consultar.
     * @return Objeto [Joven] con los datos del usuario o `null` si ocurre un error o la respuesta es inválida.
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
