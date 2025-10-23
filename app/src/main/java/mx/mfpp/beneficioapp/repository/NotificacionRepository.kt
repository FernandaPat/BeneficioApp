package mx.mfpp.beneficioapp.repository

import kotlinx.coroutines.flow.Flow
import mx.mfpp.beneficioapp.data.local.NotificacionDao
import mx.mfpp.beneficioapp.data.local.NotificacionEntity

/**
 * Repository que actúa como única fuente de verdad para las notificaciones
 * Abstrae la capa de datos y expone funciones simples para el ViewModel
 */
class NotificacionRepository(private val notificacionDao: NotificacionDao) {

    /**
     * Flow de todas las notificaciones
     * Se actualiza automáticamente cuando hay cambios en la BD
     */
    val todasLasNotificaciones: Flow<List<NotificacionEntity>> =
        notificacionDao.obtenerTodasFlow()

    /**
     * Flow de notificaciones no leídas
     */
    val notificacionesNoLeidas: Flow<List<NotificacionEntity>> =
        notificacionDao.obtenerNoLeidasFlow()

    /**
     * Flow del contador de notificaciones no leídas
     */
    val contadorNoLeidas: Flow<Int> =
        notificacionDao.contarNoLeidasFlow()

    /**
     * Guarda una nueva notificación en la base de datos
     * @return El ID de la notificación insertada
     */
    suspend fun guardarNotificacion(notificacion: NotificacionEntity): Long {
        return notificacionDao.insertar(notificacion)
    }

    /**
     * Marca una notificación como leída
     */
    suspend fun marcarComoLeida(id: Long) {
        notificacionDao.marcarComoLeida(id)
    }

    /**
     * Marca todas las notificaciones como leídas
     */
    suspend fun marcarTodasComoLeidas() {
        notificacionDao.marcarTodasComoLeidas()
    }

    /**
     * Elimina una notificación
     */
    suspend fun eliminarNotificacion(notificacion: NotificacionEntity) {
        notificacionDao.eliminar(notificacion)
    }

    /**
     * Elimina todas las notificaciones
     */
    suspend fun eliminarTodas() {
        notificacionDao.eliminarTodas()
    }
}
