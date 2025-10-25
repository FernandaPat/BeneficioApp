package mx.mfpp.beneficioapp.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Objeto (Singleton) que gestiona la comunicación con un servicio remoto
 * (posiblemente una Cloud Function) para actualizar una promoción existente.
 *
 * Utiliza [HttpURLConnection] directamente para realizar la solicitud PUT.
 */
object ServicioRemotoActualizarPromocion {

    /**
     * Envía una solicitud PUT a un endpoint específico para actualizar los
     * datos de una promoción.
     *
     * Esta operación se ejecuta en el dispatcher [Dispatchers.IO].
     *
     * @param idPromocion El ID de la promoción que se va a actualizar (se usa en la URL).
     * @param promocion El objeto [Promocion] que contiene los nuevos datos a enviar
     * (nombre, descripcion, descuento, desde, hasta, imagenUrl).
     * @return [true] si la solicitud fue exitosa (HTTP OK 200 o CREATED 201),
     * [false] si ocurre un error, una excepción, o si el código de respuesta no es exitoso.
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

            // Construye el JSON con los campos requeridos por la API
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
                // Intenta leer la respuesta exitosa
                BufferedReader(connection.inputStream.reader()).use { it.readText() }
            } catch (e: Exception) {
                // Si falla (ej. 4xx, 5xx), lee el stream de error
                BufferedReader(connection.errorStream?.reader() ?: return@withContext false).use { it.readText() }
            }

            println("📡 Respuesta HTTP $code → $responseText")

            // Retorna true si el código es 200 (OK) o 201 (Created)
            code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED
        } catch (e: Exception) {
            println("❌ Excepción al actualizar promoción: ${e.localizedMessage}")
            false
        }
    }
}