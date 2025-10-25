package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.data.local.AppDatabase
import mx.mfpp.beneficioapp.data.local.NotificacionEntity
import mx.mfpp.beneficioapp.repository.NotificacionRepository

/**
 * ViewModel para manejar notificaciones de la aplicación.
 *
 * Permite observar notificaciones, contarlas, marcar como leídas y eliminarlas.
 *
 * @param context Contexto de la aplicación para inicializar la base de datos
 */
class NotificacionesViewModel(context: Context) : ViewModel() {

    /** Repositorio encargado de operaciones sobre notificaciones */
    private val repository: NotificacionRepository

    /** Lista de notificaciones actuales */
    private val _notificaciones = MutableStateFlow<List<NotificacionEntity>>(emptyList())
    val notificaciones: StateFlow<List<NotificacionEntity>> = _notificaciones.asStateFlow()

    /** Contador de notificaciones no leídas */
    private val _contadorNoLeidas = MutableStateFlow(0)
    val contadorNoLeidas: StateFlow<Int> = _contadorNoLeidas.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(context).notificacionDao()
        repository = NotificacionRepository(dao)

        observarNotificaciones()
        observarContadorNoLeidas()
    }

    /** Observa cambios en la lista de notificaciones y actualiza el flujo correspondiente */
    private fun observarNotificaciones() {
        viewModelScope.launch {
            repository.todasLasNotificaciones.collect { lista ->
                _notificaciones.value = lista
            }
        }
    }

    /** Observa cambios en el contador de notificaciones no leídas */
    private fun observarContadorNoLeidas() {
        viewModelScope.launch {
            repository.contadorNoLeidas.collect { contador ->
                _contadorNoLeidas.value = contador
            }
        }
    }

    /**
     * Guarda una notificación en la base de datos
     *
     * @param notificacion Notificación a guardar
     */
    fun guardarNotificacion(notificacion: NotificacionEntity) {
        viewModelScope.launch {
            repository.guardarNotificacion(notificacion)
        }
    }

    /**
     * Marca una notificación específica como leída
     *
     * @param id ID de la notificación
     */
    fun marcarComoLeida(id: Long) {
        viewModelScope.launch {
            repository.marcarComoLeida(id)
        }
    }

    /** Marca todas las notificaciones como leídas */
    fun marcarTodasComoLeidas() {
        viewModelScope.launch {
            repository.marcarTodasComoLeidas()
        }
    }

    /**
     * Elimina una notificación específica
     *
     * @param notificacion Notificación a eliminar
     */
    fun eliminarNotificacion(notificacion: NotificacionEntity) {
        viewModelScope.launch {
            repository.eliminarNotificacion(notificacion)
        }
    }

    /** Elimina todas las notificaciones */
    fun eliminarTodas() {
        viewModelScope.launch {
            repository.eliminarTodas()
        }
    }
}

/**
 * Factory para crear instancias de [NotificacionesViewModel] con parámetros.
 *
 * @param context Contexto de la aplicación
 */
class NotificacionesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificacionesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificacionesViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}