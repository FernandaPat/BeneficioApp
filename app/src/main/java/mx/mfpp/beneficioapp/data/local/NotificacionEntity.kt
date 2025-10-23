package mx.mfpp.beneficioapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room para almacenar notificaciones localmente
 * Esta tabla guardará todas las notificaciones que lleguen vía FCM
 */
@Entity(tableName = "notificaciones")
data class NotificacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Información básica de la notificación
    val titulo: String,
    val mensaje: String,
    val tipo: String, // "promocion", "favorito", "general", etc.

    // Referencias a otras entidades (si aplica)
    val idPromocion: String? = null,
    val idEstablecimiento: String? = null,
    val nombreEstablecimiento: String? = null,

    // Metadatos
    val timestamp: Long = System.currentTimeMillis(),
    val leida: Boolean = false
)
