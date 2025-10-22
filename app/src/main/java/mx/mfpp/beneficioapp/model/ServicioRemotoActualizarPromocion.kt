package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL



object ServicioRemotoActualizarPromocion {

    suspend fun actualizarPromocion(
        idPromocion: Int,
        promocion: Promocion
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://actualizar-promocion-819994103285.us-central1.run.app/$idPromocion")

            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "PUT"
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.doOutput = true
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            // ✅ Estructura JSON con los nombres que Cloud Run espera
            val json = JSONObject().apply {
                put("id_promocion", idPromocion)
                put("titulo", promocion.nombre)
                put("descripcion", promocion.descripcion)
                put("descuento", promocion.descuento)
                put("fecha_inicio", promocion.desde)
                put("fecha_fin", promocion.hasta)
                put("imagen_url", promocion.imagenUrl)
            }

            // Enviar JSON
            OutputStreamWriter(connection.outputStream).use {
                it.write(json.toString())
                it.flush()
            }

            val responseCode = connection.responseCode
            val responseStream = if (responseCode in 200..299)
                connection.inputStream
            else
                connection.errorStream

            val response = responseStream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()

            println("📡 HTTP $responseCode — $response")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                true to "✅ Promoción actualizada correctamente"
            } else {
                false to "⚠️ Error $responseCode: $response"
            }

        } catch (e: Exception) {
            e.printStackTrace()
            false to "❌ Error: ${e.localizedMessage ?: "desconocido"}"
        }
    }
}
