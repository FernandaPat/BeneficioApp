package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ServicioRemotoObtenerDatosJoven {

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

                // 🟣 Aquí agregamos el log para ver la respuesta completa
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
