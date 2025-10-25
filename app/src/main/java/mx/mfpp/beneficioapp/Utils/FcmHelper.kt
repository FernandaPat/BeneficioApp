package mx.mfpp.beneficioapp.utils

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.network.NotificacionesRetrofit
import mx.mfpp.beneficioapp.network.RegistrarTokenRequest

/**
 * Objeto (Singleton) de utilidad para gestionar operaciones comunes
 * de Firebase Cloud Messaging (FCM).
 *
 * Proporciona métodos para registrar el token del dispositivo en el backend
 * y para recuperar el token almacenado localmente.
 */
object FcmHelper {
    /** Etiqueta para los logs de este helper. */
    private const val TAG = "FcmHelper"

    /**
     * Registra el token FCM actual del dispositivo en el servidor backend.
     *
     * Obtiene el token más reciente de [FirebaseMessaging] y, si tiene éxito,
     * lanza una corutina en [Dispatchers.IO] para enviarlo a la API
     * definida en [NotificacionesRetrofit].
     *
     * Esta función maneja internamente las excepciones de red y registra
     * los errores en Logcat.
     *
     * @param userId El ID del usuario (ej. `id_usuario`) que acaba de iniciar sesión,
     * necesario para asociar el token en el backend.
     */
    fun registrarTokenEnServidor(userId: Int) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            val token = task.result

            // Enviar al servidor usando coroutine en el dispatcher de IO
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = NotificacionesRetrofit.api.registrarToken(
                        RegistrarTokenRequest(
                            id_usuario = userId,
                            device_token = token,
                            plataforma = "android"
                        )
                    )
                    // (Opcional) Se podría loguear la respuesta exitosa
                    // Log.d(TAG, "✅ Token registrado: ${response.body()?.message}")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ EXCEPCIÓN AL REGISTRAR TOKEN")
                    Log.e(TAG, "❌ Tipo: ${e.javaClass.simpleName}")
                    Log.e(TAG, "❌ Mensaje: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Obtiene el token FCM guardado localmente desde [SharedPreferences].
     *
     * Nota: Este token podría estar desactualizado si [FirebaseMessagingService.onNewToken]
     * no se ha disparado recientemente o si la app fue reinstalada.
     * Es principalmente útil para fines de depuración.
     *
     * @param context El [Context] de la aplicación para acceder a [SharedPreferences].
     * @return El token FCM [String] almacenado, o `null` si no se encuentra.
     */
    fun obtenerTokenLocal(context: Context): String? {
        return context.getSharedPreferences("fcm", Context.MODE_PRIVATE)
            .getString("token", null)
    }
}