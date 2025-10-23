/**
 * Archivo: FotoRepository.kt
 *
 * Contiene la lógica para la conversión y carga de imágenes en formato Base64.
 *
 * Esta clase permite transformar imágenes seleccionadas desde el almacenamiento local
 * en cadenas codificadas Base64, y enviarlas al servidor tanto para usuarios jóvenes
 * como para establecimientos.
 */
package mx.mfpp.beneficioapp.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
/**
 * Repositorio responsable de manejar la carga y codificación de fotos.
 *
 * @param context Contexto de la aplicación, usado para acceder al content resolver.
 */
class FotoRepository(private val context: Context) {
    /**
     * Convierte una imagen obtenida desde una URI en una cadena Base64 lista para enviar al servidor.
     *
     * El resultado incluye el prefijo MIME (`data:image/jpeg;base64,`) necesario para la visualización.
     *
     * @param uri URI del archivo de imagen seleccionado por el usuario
     * @return Cadena Base64 representando la imagen
     */
    suspend fun convertirUriABase64(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
        val byteArray = outputStream.toByteArray()
        "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
    /**
     * Sube la foto de perfil de un usuario joven al servidor.
     *
     * Envía una solicitud HTTP POST con el cuerpo JSON que incluye el tipo de usuario,
     * el ID y la imagen codificada en Base64.
     *
     * @param base64 Cadena de la imagen en formato Base64
     * @param idUsuario Identificador del usuario joven
     * @return `true` si la carga fue exitosa (código HTTP 2xx), `false` en caso contrario
     */
    suspend fun subirFotoJoven(base64: String, idUsuario: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://actualizar-foto-819994103285.us-central1.run.app/")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val json = JSONObject().apply {
                    put("tipo", "joven")
                    put("id_usuario", idUsuario)
                    put("foto", base64)
                }

                conn.outputStream.use { os -> os.write(json.toString().toByteArray()) }
                val responseCode = conn.responseCode
                conn.disconnect()
                responseCode in 200..299
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    /**
     * Sube la foto de perfil de un establecimiento al servidor.
     *
     * Envía una solicitud HTTP POST con el cuerpo JSON que incluye el tipo de entidad,
     * el ID del establecimiento y la imagen codificada en Base64.
     *
     * @param base64 Cadena de la imagen en formato Base64
     * @param idEstablecimiento Identificador del establecimiento
     * @return `true` si la carga fue exitosa (código HTTP 2xx), `false` en caso contrario
     */
    suspend fun subirFotoNegocio(base64: String, idEstablecimiento: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://actualizar-foto-819994103285.us-central1.run.app/")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val json = JSONObject().apply {
                    put("tipo", "establecimiento")
                    put("id_establecimiento", idEstablecimiento)
                    put("foto", base64)
                }

                conn.outputStream.use { os -> os.write(json.toString().toByteArray()) }
                val responseCode = conn.responseCode
                conn.disconnect()
                responseCode in 200..299
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

}
