package mx.mfpp.beneficioapp.utils

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.network.NotificacionesRetrofit
import mx.mfpp.beneficioapp.network.RegistrarTokenRequest

object FcmHelper {
    private const val TAG = "FcmHelper"

    /**
     * Registra el token FCM del dispositivo en el servidor.
     * Se debe llamar después de un login exitoso.
     *
     * @param userId ID del usuario que acaba de hacer login
     */
    fun registrarTokenEnServidor(userId: Int) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            val token = task.result

            // Enviar al servidor usando coroutine
            CoroutineScope(Dispatchers.IO).launch {
                try {

                    val response = NotificacionesRetrofit.api.registrarToken(
                        RegistrarTokenRequest(
                            id_usuario = userId,
                            device_token = token,
                            plataforma = "android"
                        )
                    )
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
     * Obtiene el token FCM guardado localmente.
     * Útil para debugging o verificación.
     */
    fun obtenerTokenLocal(context: Context): String? {
        return context.getSharedPreferences("fcm", Context.MODE_PRIVATE)
            .getString("token", null)
    }
}