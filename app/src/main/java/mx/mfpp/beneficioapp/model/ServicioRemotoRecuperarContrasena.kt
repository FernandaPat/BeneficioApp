package mx.mfpp.beneficioapp.model

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Servicio remoto para manejar operaciones de recuperación de contraseña.
 *
 * Responsabilidades:
 * - Obtener el email de un usuario (joven o establecimiento) desde el servidor
 * - Manejar las peticiones HTTP a las Cloud Functions
 */
object ServicioRemotoRecuperarContrasena {


    private const val CLOUD_FUNCTION_URL = "https://obtener-email-joven-819994103285.us-central1.run.app"
    private const val TAG = "RecuperarRepository"

    /**
     * Obtiene el email de un joven desde el servidor.
     *
     * @param idJoven ID del joven
     * @return EmailResponse con los datos del joven, o null si hubo error
     */
    suspend fun obtenerEmailJoven(idJoven: Int): EmailResponse? = withContext(Dispatchers.IO) {
        return@withContext obtenerEmail(idJoven = idJoven)
    }

    /**
     * Obtiene el email de un establecimiento desde el servidor.
     *
     * @param idEstablecimiento ID del establecimiento (admin)
     * @return EmailResponse con los datos del establecimiento, o null si hubo error
     */
    suspend fun obtenerEmailEstablecimiento(idEstablecimiento: Int): EmailResponse? = withContext(Dispatchers.IO) {
        return@withContext obtenerEmail(idEstablecimiento = idEstablecimiento)
    }

    /**
     * Función privada que hace la petición HTTP.
     */
    private suspend fun obtenerEmail(
        idJoven: Int? = null,
        idEstablecimiento: Int? = null
    ): EmailResponse? = withContext(Dispatchers.IO) {
        try {
            // Construir URL con el parámetro correcto
            val urlString = if (idJoven != null) {
                "$CLOUD_FUNCTION_URL?id_joven=$idJoven"
            } else if (idEstablecimiento != null) {
                "$CLOUD_FUNCTION_URL?id_establecimiento=$idEstablecimiento"
            } else {
                Log.e(TAG, "❌ No se proporcionó id_joven ni id_establecimiento")
                return@withContext null
            }

            val tipoUsuario = if (idJoven != null) "joven" else "establecimiento"
            val id = idJoven ?: idEstablecimiento!!

            Log.d(TAG, "🔍 Obteniendo email para $tipoUsuario ID: $id")

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.doInput = true

            val code = connection.responseCode
            val responseText = try {
                BufferedReader(connection.inputStream.reader()).use { it.readText() }
            } catch (e: Exception) {
                BufferedReader(connection.errorStream?.reader() ?: return@withContext null).use { it.readText() }
            }

            Log.d(TAG, "📡 Respuesta HTTP $code → $responseText")

            if (code == HttpURLConnection.HTTP_OK) {
                val jsonResponse = JSONObject(responseText)
                val emailResponse = EmailResponse(
                    email = jsonResponse.getString("email"),
                    nombre = jsonResponse.optString("nombre", ""),
                    apellidos = jsonResponse.optString("apellidos", ""),
                    tipoUsuario = jsonResponse.optString("tipo_usuario", tipoUsuario)
                )

                Log.d(TAG, "✅ Email obtenido: ${emailResponse.email} (${emailResponse.tipoUsuario})")
                return@withContext emailResponse

            } else {
                Log.e(TAG, "❌ Error HTTP $code: $responseText")
                return@withContext null
            }

        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "❌ Timeout en la petición: ${e.message}")
            return@withContext null

        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "❌ No hay conexión a internet: ${e.message}")
            return@withContext null

        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción al obtener email: ${e.localizedMessage}")
            e.printStackTrace()
            return@withContext null
        }
    }

    /**
     * Verifica si el servicio está disponible.
     *
     * @return true si el servicio responde, false en caso contrario
     */
    suspend fun verificarDisponibilidad(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL(CLOUD_FUNCTION_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val isAvailable = connection.responseCode in 200..499
            Log.d(TAG, if (isAvailable) "✅ Servicio disponible" else "❌ Servicio no disponible")
            return@withContext isAvailable

        } catch (e: Exception) {
            Log.e(TAG, "❌ Servicio no disponible: ${e.message}")
            return@withContext false
        }
    }
}

/**
 * Data class que representa la respuesta del servidor con los datos del usuario.
 *
 * @property email Correo electrónico del usuario
 * @property nombre Nombre del usuario
 * @property apellidos Apellidos completos del usuario
 * @property tipoUsuario Tipo de usuario: "joven" o "establecimiento"
 */
data class EmailResponse(
    val email: String,
    val nombre: String,
    val apellidos: String,
    val tipoUsuario: String = "joven"
)