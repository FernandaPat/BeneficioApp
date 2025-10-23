package mx.mfpp.beneficioapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operaciones con notificaciones
 * Todas las funciones suspend se ejecutan en un thread de IO automáticamente
 */
@Dao
interface NotificacionDao {

    /**
     * Obtiene todas las notificaciones ordenadas por fecha (más recientes primero)
     * Retorna un Flow para observar cambios en tiempo real
     */
    @Query("SELECT * FROM notificaciones ORDER BY timestamp DESC")
    fun obtenerTodasFlow(): Flow<List<NotificacionEntity>>

    /**
     * Obtiene solo las notificaciones no leídas
     */
    @Query("SELECT * FROM notificaciones WHERE leida = 0 ORDER BY timestamp DESC")
    fun obtenerNoLeidasFlow(): Flow<List<NotificacionEntity>>

    /**
     * Cuenta cuántas notificaciones no leídas hay
     * Útil para mostrar el badge en el ícono de notificaciones
     */
    @Query("SELECT COUNT(*) FROM notificaciones WHERE leida = 0")
    fun contarNoLeidasFlow(): Flow<Int>

    /**
     * Inserta una nueva notificación
     * Si ya existe (mismo ID), la reemplaza
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(notificacion: NotificacionEntity): Long

    /**
     * Marca una notificación como leída
     */
    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarComoLeida(id: Long)

    /**
     * Marca todas las notificaciones como leídas
     */
    @Query("UPDATE notificaciones SET leida = 1")
    suspend fun marcarTodasComoLeidas()

    /**
     * Elimina una notificación específica
     */
    @Delete
    suspend fun eliminar(notificacion: NotificacionEntity)

    /**
     * Elimina todas las notificaciones
     */
    @Query("DELETE FROM notificaciones")
    suspend fun eliminarTodas()
}