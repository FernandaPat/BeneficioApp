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
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.view.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Guardar token en SharedPreferences
        getSharedPreferences("fcm", Context.MODE_PRIVATE).edit {
            putString("token", token)
        }

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // ✅ LEER DATOS (ahora título y mensaje están en data)
        val idPromocion = message.data["id_promocion"]
        val nombreEstablecimiento = message.data["nombre_establecimiento"]
        val titulo = message.data["titulo"] ?: "Nueva promoción"  // ← Desde data
        val cuerpo = message.data["mensaje"] ?: ""                 // ← Desde data

        // ✅ MOSTRAR NOTIFICACIÓN (siempre, en cualquier estado de la app)
        mostrarNotificacion(
            titulo = titulo,
            mensaje = cuerpo,
            idPromocion = idPromocion,
            nombreEstablecimiento = nombreEstablecimiento
        )
    }

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
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Cambiar por tu icono
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

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)

    }

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "promociones_channel"
    }
}