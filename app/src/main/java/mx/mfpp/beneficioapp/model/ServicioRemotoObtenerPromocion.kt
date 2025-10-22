package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ServicioRemotoObtenerPromocion {

    suspend fun obtenerPromocionPorId(idPromocion: Int): Promocion? = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://listar-promociones-819994103285.us-central1.run.app"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                println("⚠️ Error HTTP $responseCode al cargar promoción")
                return@withContext null
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)
            val data = json.getJSONArray("data")

            for (i in 0 until data.length()) {
                val item = data.getJSONObject(i)
                if (item.getInt("id") == idPromocion) {
                    return@withContext Promocion(
                        id = item.getInt("id"),
                        nombre = item.optString("titulo", ""),
                        descripcion = item.optString("descripcion", ""),
                        descuento = item.optString("descuento", ""),
                        desde = item.optString("fecha_inicio", ""),
                        hasta = item.optString("fecha_fin", ""),
                        imagenUrl = item.optString("imagen_url", "")
                    )
                }
            }

            println("⚠️ No se encontró promoción con ID $idPromocion")
            null
        } catch (e: Exception) {
            println("❌ Excepción al obtener promoción: ${e.localizedMessage}")
            null
        }
    }
}
