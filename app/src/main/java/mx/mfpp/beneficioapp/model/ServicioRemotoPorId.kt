/**
 * Archivo: ServicioRemotoPromocionPorId.kt
 *
 * Define un servicio remoto encargado de obtener los datos de una promoción específica
 * desde el servidor, utilizando su identificador único como parámetro.
 *
 * Utiliza `HttpURLConnection` para ejecutar una solicitud HTTP GET y analiza la respuesta
 * JSON para devolver un objeto [PromocionRemota].
 */
package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
/**
 * Representa la estructura de una promoción remota obtenida desde el backend.
 *
 * Este modelo refleja los campos devueltos por el endpoint del servidor.
 *
 * @property id Identificador único de la promoción
 * @property titulo Título o nombre de la promoción
 * @property descripcion Descripción breve de la promoción
 * @property descuento Porcentaje o tipo de descuento ofrecido
 * @property disponible_desde Fecha de inicio de la promoción
 * @property hasta Fecha de finalización de la promoción
 * @property imagen URL o ruta de la imagen asociada a la promoción
 */
data class PromocionRemota(
    val id: Int?,
    val titulo: String?,
    val descripcion: String?,
    val descuento: String?,
    val disponible_desde: String?,
    val hasta: String?,
    val imagen: String?
)
/**
 * Objeto singleton responsable de la comunicación con el servicio remoto
 * que permite obtener los datos de una promoción por su identificador.
 *
 * Implementa lógica de conexión y parseo de respuesta JSON de forma asíncrona.
 */
object ServicioRemotoPromocionPorId {
    /**
     * Obtiene la información detallada de una promoción desde el servidor según su ID.
     *
     * Realiza una solicitud HTTP GET al endpoint remoto con el parámetro `id_promocion`.
     * En caso de respuesta exitosa, construye y devuelve un objeto [PromocionRemota]
     * con los datos obtenidos. Si ocurre un error o la respuesta está vacía, devuelve
     * una instancia vacía de [PromocionRemota].
     *
     * @param idPromocion Identificador único de la promoción que se desea consultar.
     * @return Objeto [PromocionRemota] con los datos recibidos o con valores nulos si hubo error.
     */
    suspend fun obtenerPromocionPorId(idPromocion: Int): PromocionRemota {
        return withContext(Dispatchers.IO) {
            val endpoint = "https://listar-promociones-819994103285.us-central1.run.app/?id_promocion=$idPromocion"

            val url = URL(endpoint)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            try {
                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("📡 Respuesta del servidor al obtener promoción por ID: $response")

                if (responseCode in 200..299) {

                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {
                        val json = jsonArray.getJSONObject(0)
                        return@withContext PromocionRemota(
                            id = json.optInt("id_promocion"),
                            titulo = json.optString("titulo"),
                            descripcion = json.optString("descripcion"),
                            descuento = json.optString("descuento"),
                            disponible_desde = json.optString("disponible_desde"),
                            hasta = json.optString("hasta"),
                            imagen = json.optString("imagen")
                        )
                    }
                }
                PromocionRemota(null, null, null, null, null, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
                PromocionRemota(null, null, null, null, null, null, null)
            } finally {
                connection.disconnect()
            }
        }
    }
}
