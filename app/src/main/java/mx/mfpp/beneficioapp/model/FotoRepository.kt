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

class FotoRepository(private val context: Context) {

    suspend fun convertirUriABase64(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
        val byteArray = outputStream.toByteArray()
        "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

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
