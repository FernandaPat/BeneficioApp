package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ServicioRemotoObtenerDatosNegocio {

    suspend fun obtenerDatosNegocio(idEstablecimiento: Int): Negocio? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://obtener-datos-negocio-819994103285.us-central1.run.app/?id_establecimiento=$idEstablecimiento")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    println("❌ Error HTTP $responseCode")
                    return@withContext null
                }

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("🟣 RESPUESTA NEGOCIO → $response")

                val json = JSONObject(response)
                val data = json.optJSONObject("data") ?: return@withContext null

                return@withContext Negocio(
                    id = data.optInt("id", 0),
                    nombre = data.optString("nombre"),
                    correo = data.optString("correo", ""),
                    telefono = data.optString("numero_de_telefono", ""),
                    direccion = data.optString("direccion"),
                    categoria = data.optString("categoria", "")
                )
            } catch (e: Exception) {
                println("❌ Error al obtener datos del negocio: ${e.localizedMessage}")
                null
            }
        }
    }
}
