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
 * Repositorio encargado de gestionar las operaciones relacionadas con fotografías,
 * como la conversión de Uris a Base64 y la subida de imágenes al servidor.
 *
 * @property context El contexto de la aplicación, necesario para acceder
 * al ContentResolver y leer los datos de la Uri.
 */
class FotoRepository(private val context: Context) {

    /**
     * Convierte una Uri de una imagen (obtenida del ContentResolver) en una
     * cadena de texto en formato Base64 con el prefijo "data:image/jpeg;base64,".
     *
     * La imagen se comprime como JPEG con calidad del 90%.
     * Esta operación se ejecuta en el dispatcher [Dispatchers.IO].
     *
     * @param uri La [Uri] del archivo de imagen a convertir.
     * @return La cadena de texto [String] en formato Base64.
     */
    suspend fun convertirUriABase64(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        // Comprime el bitmap a JPEG con 90% de calidad
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
        val byteArray = outputStream.toByteArray()
        // Codifica a Base64 y añade el prefijo MIME type
        "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /**
     * Sube la foto de perfil de un usuario (joven) al servidor.
     *
     * Utiliza un endpoint específico y envía un JSON con el tipo "joven",
     * el ID del usuario y la imagen en Base64.
     * Esta operación se ejecuta en el dispatcher [Dispatchers.IO].
     *
     * @param base64 La imagen codificada en Base64 (debe incluir el prefijo data:image/jpeg;base64,).
     * @param idUsuario El identificador único del usuario al que pertenece la foto.
     * @return [true] si la subida fue exitosa (código de respuesta HTTP 2xx),
     * [false] en caso contrario o si ocurre una excepción.
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
                responseCode in 200..299 // Retorna true si el código es 2xx
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    /**
     * Sube la foto de un establecimiento o negocio al servidor.
     *
     * Utiliza el mismo endpoint que [subirFotoJoven] pero envía un JSON
     * con el tipo "establecimiento" y el ID del establecimiento.
     * Esta operación se ejecuta en el dispatcher [Dispatchers.IO].
     *
     * @param base64 La imagen codificada en Base64 (debe incluir el prefijo data:image/jpeg;base64,).
     * @param idEstablecimiento El identificador único del establecimiento al que pertenece la foto.
     * @return [true] si la subida fue exitosa (código de respuesta HTTP 2xx),
     * [false] en caso contrario o si ocurre una excepción.
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
                responseCode in 200..299 // Retorna true si el código es 2xx
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

}