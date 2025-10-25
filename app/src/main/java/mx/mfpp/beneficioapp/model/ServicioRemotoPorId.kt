package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Modelo de datos que representa la estructura de una promoción
 * tal como se recibe del endpoint 'listar-promociones' cuando
 * se filtra por ID.
 *
 * Todas las propiedades son nulas para manejar respuestas fallidas o incompletas.
 *
 * @property id El identificador único de la promoción.
 * @property titulo El título de la promoción.
 * @property descripcion La descripción de la promoción.
 * @property descuento El texto del descuento.
 * @property disponible_desde La fecha (String) de inicio de vigencia.
 * @property hasta La fecha (String) de fin de vigencia.
 * @property imagen La URL de la imagen de la promoción.
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
 * Objeto (Singleton) que gestiona la comunicación con un servicio remoto
 * (posiblemente una Cloud Function) para obtener los datos
 * de una promoción específica por su ID.
 *
 * Utiliza [HttpURLConnection] directamente para realizar la solicitud GET.
 */
object ServicioRemotoPromocionPorId {

    /**
     * Realiza una solicitud GET a un endpoint específico para obtener
     * los detalles de una promoción usando su ID.
     *
     * Esta operación se ejecuta en el dispatcher [Dispatchers.IO].
     *
     * El endpoint esperado (Cloud Function) filtra por `id_promocion` y
     * devuelve un JSONArray que contiene (idealmente) un solo objeto.
     *
     * @param idPromocion El identificador único de la promoción que se desea buscar.
     * @return Un objeto [PromocionRemota] poblado con los datos obtenidos
     * si la solicitud HTTP es 2xx y la respuesta (JSONArray) contiene
     * al menos un elemento.
     * Retorna un [PromocionRemota] con todos los campos nulos si
     * la respuesta HTTP no es exitosa, ocurre una excepción, o la
     * respuesta es un array vacío.
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
                    // El endpoint devuelve un Array, incluso si es un solo ítem
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
                // Retorna objeto vacío (nulos) si no fue 2xx o el array estaba vacío
                PromocionRemota(null, null, null, null, null, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
                // Retorna objeto vacío (nulos) en caso de excepción
                PromocionRemota(null, null, null, null, null, null, null)
            } finally {
                connection.disconnect()
            }
        }
    }
}