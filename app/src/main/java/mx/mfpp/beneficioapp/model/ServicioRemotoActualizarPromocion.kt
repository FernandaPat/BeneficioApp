package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object ServicioRemotoActualizarPromocion {

    suspend fun actualizarPromocion(idPromocion: Int, promocion: Promocion): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://actualizar-promocion-819994103285.us-central1.run.app/$idPromocion")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val json = JSONObject().apply {
                    put("id_promocion", idPromocion)
                    put("titulo", promocion.nombre)
                    put("descripcion", promocion.descripcion)
                    put("descuento", promocion.descuento)
                    put("fecha_inicio", promocion.desde)
                    put("fecha_fin", promocion.hasta)
                    put("imagen_url", promocion.imagenUrl)
                }

                val writer = BufferedWriter(OutputStreamWriter(connection.outputStream))
                writer.write(json.toString())
                writer.flush()
                writer.close()

                val code = connection.responseCode
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                println("📡 HTTP $code → $response")

                return@withContext code in 200..299
            } catch (e: Exception) {
                println("❌ Error al actualizar promoción: ${e.message}")
                return@withContext false
            }
        }
    }
}
