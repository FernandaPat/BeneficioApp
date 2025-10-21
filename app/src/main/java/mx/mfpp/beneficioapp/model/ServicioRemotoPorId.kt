package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class PromocionRemota(
    val id: Int?,
    val titulo: String?,
    val descripcion: String?,
    val descuento: String?,
    val disponible_desde: String?,
    val hasta: String?,
    val imagen: String?
)

object ServicioRemotoPromocionPorId {

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
                    // ✅ Si viene como lista
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
