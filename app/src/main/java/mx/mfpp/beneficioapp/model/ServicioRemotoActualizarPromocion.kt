/**
 * Archivo: ServicioRemotoActualizarPromocion.kt
 *
 * Define un servicio remoto encargado de actualizar la información de una promoción
 * existente en el servidor mediante una solicitud HTTP PUT.
 *
 * Este servicio utiliza `HttpURLConnection` y `kotlinx.coroutines` para realizar
 * la operación de forma asíncrona en un hilo de E/S (IO).
 */
package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
/**
 * Objeto singleton responsable de enviar solicitudes al servidor para actualizar promociones.
 *
 * Utiliza una función suspendida que construye y envía una solicitud HTTP PUT
 * con los datos de la promoción en formato JSON.
 */
object ServicioRemotoActualizarPromocion {
    /**
     * Actualiza los datos de una promoción existente en el servidor.
     *
     * Envía una solicitud HTTP PUT al endpoint correspondiente con un cuerpo JSON
     * que contiene los campos actualizados de la promoción.
     *
     * @param idPromocion Identificador único de la promoción a actualizar
     * @param promocion Objeto [Promocion] con los datos actualizados de la promoción
     * @return `true` si la actualización fue exitosa (HTTP 200 o 201), `false` en caso contrario
     */
    suspend fun actualizarPromocion(idPromocion: Int, promocion: Promocion): Boolean = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://actualizar-promocion-819994103285.us-central1.run.app/$idPromocion"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "PUT"
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.doOutput = true
            connection.doInput = true


            // Construir el cuerpo JSON según lo esperado por la API
            val json = JSONObject().apply {
                put("titulo", promocion.nombre)
                put("descripcion", promocion.descripcion)
                put("descuento", promocion.descuento)
                put("disponible_desde", promocion.desde)
                put("hasta", promocion.hasta)
                put("imagen", promocion.imagenUrl)
            }

            println("📤 JSON enviado → $json")

            // Enviar cuerpo al servidor
            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use {
                it.write(json.toString())
                it.flush()
            }

            val code = connection.responseCode
            val responseText = try {
                BufferedReader(connection.inputStream.reader()).use { it.readText() }
            } catch (e: Exception) {
                BufferedReader(connection.errorStream?.reader() ?: return@withContext false).use { it.readText() }
            }

            println("📡 Respuesta HTTP $code → $responseText")

            code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED
        } catch (e: Exception) {
            println("❌ Excepción al actualizar promoción: ${e.localizedMessage}")
            false
        }
    }
}
