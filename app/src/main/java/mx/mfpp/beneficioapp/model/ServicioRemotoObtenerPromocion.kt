package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Objeto (Singleton) que gestiona la comunicación con un servicio remoto
 * (posiblemente una Cloud Function) para obtener los datos
 * de una promoción específica por su ID.
 *
 * Utiliza [HttpURLConnection] directamente para realizar la solicitud GET.
 *
 * **Nota de Implementación:** Este servicio descarga la lista *completa* de
 * promociones del endpoint y la filtra localmente para encontrar la
 * promoción solicitada.
 */
object ServicioRemotoObtenerPromocion {

    /**
     * Obtiene los detalles de una promoción específica consultando su ID.
     *
     * Esta operación se ejecuta en el dispatcher [Dispatchers.IO].
     *
     * **Advertencia:** Este método descarga la lista *entera* de promociones
     * (desde `.../listar-promociones...`) y luego itera sobre ella
     * localmente para encontrar la que coincida con el [idPromocion].
     *
     * @param idPromocion El identificador único de la promoción que se desea buscar.
     * @return Un objeto [Promocion] si se encuentra en la lista y la solicitud
     * es exitosa (HTTP 200).
     * Retorna `null` si la respuesta HTTP no es 200, si ocurre una
     * excepción de red, o si ninguna promoción en la lista coincide con el ID.
     */
    suspend fun obtenerPromocionPorId(idPromocion: Int): Promocion? = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://listar-promociones-819994103285.us-central1.run.app"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                println("⚠️ Error HTTP ${connection.responseCode} al cargar promoción")
                return@withContext null
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)
            val data = json.getJSONArray("data")

            // Itera sobre la lista completa para encontrar el ID
            for (i in 0 until data.length()) {
                val item = data.getJSONObject(i)
                if (item.getInt("id") == idPromocion) {
                    val promo = Promocion(
                        id = item.getInt("id"),
                        nombre = item.optString("titulo", ""),
                        descripcion = item.optString("descripcion", ""),
                        descuento = item.optString("descuento", ""),
                        desde = item.optString("disponible_desde", ""),
                        hasta = item.optString("hasta", ""),
                        imagenUrl = item.optString("imagen", "")
                    )
                    println("✅ Promoción encontrada: ${promo.nombre}, desde: ${promo.desde}, hasta: ${promo.hasta}, img: ${promo.imagenUrl}")
                    return@withContext promo
                }
            }

            println("⚠️ No se encontró promoción con ID: $idPromocion")
            null
        } catch (e: Exception) {
            println("❌ Excepción al obtener promoción: ${e.localizedMessage}")
            null
        }
    }
}