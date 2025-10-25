package mx.mfpp.beneficioapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.data.local.AppDatabase
import mx.mfpp.beneficioapp.data.local.NotificacionEntity
import mx.mfpp.beneficioapp.view.MainActivity

/**
 * Servicio que extiende [FirebaseMessagingService] para manejar la recepción
 * de notificaciones push de Firebase Cloud Messaging (FCM).
 *
 * Es responsable de:
 * 1. Recibir nuevos tokens de registro de FCM ([onNewToken]).
 * 2. Procesar mensajes de datos entrantes ([onMessageReceived]).
 * 3. Guardar las notificaciones recibidas en la base de datos local (Room).
 * 4. Mostrar una notificación visible al usuario en la barra de estado.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Se llama cuando FCM genera un nuevo token de registro para el dispositivo
     * o cuando el token existente es invalidado.
     *
     * El nuevo token se guarda en [SharedPreferences] (modo privado, "fcm")
     * para su uso posterior (ej. enviarlo al servidor backend).
     *
     * @param token El nuevo token de registro del dispositivo.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Guardar token en SharedPreferences
        getSharedPreferences("fcm", Context.MODE_PRIVATE).edit {
            putString("token", token)
        }

    }

    /**
     * Se llama cuando se recibe un mensaje de datos (data message) de FCM
     * mientras la app está en primer plano o en segundo plano.
     *
     * Extrae los datos personalizados del payload (`message.data`),
     * los guarda en la base de datos Room y luego muestra una
     * notificación al usuario.
     *
     * @param message El objeto [RemoteMessage] que contiene los datos de la notificación.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Extrae los campos del payload de 'data'
        val idPromocion = message.data["id_promocion"]
        val idEstablecimiento = message.data["id_establecimiento"]
        val nombreEstablecimiento = message.data["nombre_establecimiento"]
        val titulo = message.data["titulo"] ?: "Nueva promoción"
        val cuerpo = message.data["mensaje"] ?: ""
        val tipo = message.data["tipo"] ?: "promocion"


        Log.d(TAG, "📬 Notificación recibida")
        Log.d(TAG, "📝 Título: $titulo")
        Log.d(TAG, "📝 Mensaje: $cuerpo")
        Log.d(TAG, "🏪 Establecimiento: $nombreEstablecimiento")

        // Persiste la notificación en la base de datos local
        guardarNotificacionEnRoom(
            titulo = titulo,
            mensaje = cuerpo,
            tipo = tipo,
            idPromocion = idPromocion,
            idEstablecimiento = idEstablecimiento,
            nombreEstablecimiento = nombreEstablecimiento
        )

        // Muestra la notificación en la barra de estado
        mostrarNotificacion(
            titulo = titulo,
            mensaje = cuerpo,
            idPromocion = idPromocion,
            nombreEstablecimiento = nombreEstablecimiento
        )
    }

    /**
     * Guarda los detalles de la notificación recibida en la base de datos Room
     * [AppDatabase] usando una corutina en [Dispatchers.IO].
     *
     * @param titulo El título de la notificación.
     * @param mensaje El cuerpo (mensaje) de la notificación.
     * @param tipo El tipo de notificación (ej. "promocion").
     * @param idPromocion El ID de la promoción asociada (opcional).
     * @param idEstablecimiento El ID del establecimiento asociado (opcional).
     * @param nombreEstablecimiento El nombre del establecimiento (opcional).
     */
    private fun guardarNotificacionEnRoom(
        titulo: String,
        mensaje: String,
        tipo: String,
        idPromocion: String?,
        idEstablecimiento: String?,
        nombreEstablecimiento: String?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val notificacion = NotificacionEntity(
                    titulo = titulo,
                    mensaje = mensaje,
                    tipo = tipo,
                    idPromocion = idPromocion,
                    idEstablecimiento = idEstablecimiento,
                    nombreEstablecimiento = nombreEstablecimiento,
                    timestamp = System.currentTimeMillis(),
                    leida = false
                )

                val id = db.notificacionDao().insertar(notificacion)
                Log.d(TAG, "✅ Notificación guardada en Room con ID: $id")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al guardar en Room: ${e.message}", e)
            }
        }
    }

    /**
     * Construye y muestra una notificación en la barra de estado del sistema.
     *
     * Crea un [NotificationChannel] (requerido para Android 8.0+).
     * Define un [PendingIntent] para abrir [MainActivity] cuando el usuario
     * toque la notificación, pasando extras (como `id_promocion`).
     *
     * @param titulo El título a mostrar en la notificación.
     * @param mensaje El cuerpo (mensaje) a mostrar.
     * @param idPromocion El ID de la promoción (opcional) para pasarlo como extra
     * al [PendingIntent].
     * @param nombreEstablecimiento El nombre del establecimiento (opcional)
     * para pasarlo como extra.
     */
    private fun mostrarNotificacion(
        titulo: String,
        mensaje: String,
        idPromocion: String?,
        nombreEstablecimiento: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Promociones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de nuevas promociones"
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#9605F7")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app al tocar la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("id_promocion", idPromocion)
            putExtra("nombre_establecimiento", nombreEstablecimiento)
            putExtra("open_from_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Crear notificación
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Icono de la app
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_PROMO)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(android.graphics.Color.parseColor("#9605F7"))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        // ID único para la notificación (basado en el tiempo)
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)

    }

    companion object {
        /** Etiqueta para los logs de este servicio. */
        private const val TAG = "FCMService"
        /** ID único para el canal de notificaciones de promociones. */
        private const val CHANNEL_ID = "promociones_channel"
    }
}