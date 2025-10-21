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
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d(TAG, "🔔 FCM: Iniciando registro de token")
        Log.d(TAG, "👤 Usuario ID: $userId")
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "❌ Error obteniendo token FCM", task.exception)
                Log.e(TAG, "❌ Excepción: ${task.exception?.message}")
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(TAG, "✅ Token FCM obtenido del dispositivo")
            Log.d(TAG, "🔑 Preview: ${token.take(30)}...")
            Log.d(TAG, "📏 Longitud: ${token.length} caracteres")

            // Enviar al servidor usando coroutine
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(TAG, "📤 Enviando token al servidor...")
                    Log.d(TAG, "🌐 URL: https://registrartoken-819994103285-us-central1.a.run.app/")

                    val response = NotificacionesRetrofit.api.registrarToken(
                        RegistrarTokenRequest(
                            id_usuario = userId,
                            device_token = token,
                            plataforma = "android"
                        )
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                        Log.d(TAG, "✅ TOKEN REGISTRADO EN SERVIDOR")
                        Log.d(TAG, "📤 Mensaje: ${body?.message}")
                        Log.d(TAG, "👤 User ID: ${body?.id_usuario}")
                        Log.d(TAG, "📱 Plataforma: ${body?.plataforma}")
                        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    } else {
                        Log.e(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                        Log.e(TAG, "❌ ERROR AL REGISTRAR TOKEN")
                        Log.e(TAG, "❌ Código HTTP: ${response.code()}")
                        Log.e(TAG, "❌ Mensaje: ${response.message()}")
                        Log.e(TAG, "❌ Body: ${response.errorBody()?.string()}")
                        Log.e(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    Log.e(TAG, "❌ EXCEPCIÓN AL REGISTRAR TOKEN")
                    Log.e(TAG, "❌ Tipo: ${e.javaClass.simpleName}")
                    Log.e(TAG, "❌ Mensaje: ${e.message}")
                    Log.e(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
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