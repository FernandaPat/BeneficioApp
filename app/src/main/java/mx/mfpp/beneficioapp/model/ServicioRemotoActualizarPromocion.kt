package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ServicioRemotoActualizarPromocion {

    suspend fun actualizarPromocion(
        idPromocion: Int,
        titulo: String,
        descripcion: String,
        descuento: String,
        disponibleDesde: String?,
        hasta: String?,
        imagenBase64: String?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val endpoint = "https://actualizar-promocion-819994103285.us-central1.run.app/$idPromocion"
            val url = URL(endpoint)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "PUT"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // 🧠 Aseguramos formato dd/MM/yyyy
            val formatoApi = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            fun normalizarFecha(fecha: String?): String? {
                if (fecha.isNullOrBlank()) return null
                return try {
                    // Si viene en formato ISO (yyyy-MM-dd), la convertimos
                    if (fecha.contains("-")) {
                        LocalDate.parse(fecha).format(formatoApi)
                    } else fecha // ya está en dd/MM/yyyy
                } catch (e: Exception) {
                    println("⚠️ Error al formatear fecha: ${e.message}")
                    fecha
                }
            }

            val jsonBody = JSONObject().apply {
                put("titulo", titulo)
                put("descripcion", descripcion)
                put("descuento", descuento)
                normalizarFecha(disponibleDesde)?.let { put("disponible_desde", it) }
                normalizarFecha(hasta)?.let { put("hasta", it) }
                imagenBase64?.let { put("imagen", it) }
            }

            println("📦 JSON enviado: $jsonBody")

            connection.outputStream.use { os ->
                val input = jsonBody.toString().toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            val responseText = try {
                connection.inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: e.message
            }

            println("🔁 PUT $endpoint")
            println("📡 Código HTTP: $responseCode")
            println("📡 Respuesta servidor: $responseText")

            responseCode in 200..299
        }
    }
}
