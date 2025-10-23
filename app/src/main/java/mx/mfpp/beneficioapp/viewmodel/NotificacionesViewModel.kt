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

class NotificacionesViewModel(context: Context) : ViewModel() {

    private val repository: NotificacionRepository

    private val _notificaciones = MutableStateFlow<List<NotificacionEntity>>(emptyList())
    val notificaciones: StateFlow<List<NotificacionEntity>> = _notificaciones.asStateFlow()

    private val _contadorNoLeidas = MutableStateFlow(0)
    val contadorNoLeidas: StateFlow<Int> = _contadorNoLeidas.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(context).notificacionDao()
        repository = NotificacionRepository(dao)

        observarNotificaciones()
        observarContadorNoLeidas()
    }

    private fun observarNotificaciones() {
        viewModelScope.launch {
            repository.todasLasNotificaciones.collect { lista ->
                _notificaciones.value = lista
            }
        }
    }

    private fun observarContadorNoLeidas() {
        viewModelScope.launch {
            repository.contadorNoLeidas.collect { contador ->
                _contadorNoLeidas.value = contador
            }
        }
    }

    fun guardarNotificacion(notificacion: NotificacionEntity) {
        viewModelScope.launch {
            repository.guardarNotificacion(notificacion)
        }
    }

    fun marcarComoLeida(id: Long) {
        viewModelScope.launch {
            repository.marcarComoLeida(id)
        }
    }

    fun marcarTodasComoLeidas() {
        viewModelScope.launch {
            repository.marcarTodasComoLeidas()
        }
    }

    fun eliminarNotificacion(notificacion: NotificacionEntity) {
        viewModelScope.launch {
            repository.eliminarNotificacion(notificacion)
        }
    }

    fun eliminarTodas() {
        viewModelScope.launch {
            repository.eliminarTodas()
        }
    }
}

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