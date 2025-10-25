// FavoritosViewModel.kt - VERSIÓN CORREGIDA
package mx.mfpp.beneficioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.ServicioRemotoFavoritos
import mx.mfpp.beneficioapp.model.SessionManager
/**
 * ViewModel para manejar la gestión de favoritos de los establecimientos.
 *
 * Permite agregar o eliminar favoritos para el usuario actual,
 * controlar el estado de carga y manejar mensajes de error o éxito.
 *
 * @param sessionManager Gestión de sesión del usuario.
 */
class FavoritosViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    /** Indica si se está realizando alguna operación de carga sobre favoritos */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Mensaje de éxito o error al manipular favoritos */
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    /** Conjunto de IDs de establecimientos con operaciones de favoritos en progreso */
    private val _operacionesEnProgreso = mutableSetOf<Int>()

    /**
     * Agrega o elimina un establecimiento de los favoritos del usuario.
     *
     * Evita operaciones concurrentes sobre el mismo establecimiento
     * y actualiza tanto la lista local como cualquier detalle de UI.
     *
     * @param idEstablecimiento ID del establecimiento a agregar/eliminar de favoritos.
     * @param esFavoritoActual Estado actual de favorito del establecimiento.
     * @param busquedaViewModel ViewModel opcional de búsqueda para actualizar la UI local.
     * @param onUpdateDetalle Callback opcional para actualizar UI de detalle.
     */
    fun toggleFavorito(
        idEstablecimiento: Int,
        esFavoritoActual: Boolean,
        busquedaViewModel: BusquedaViewModel? = null,
        onUpdateDetalle: ((Boolean) -> Unit)? = null
    ) {
        if (_operacionesEnProgreso.contains(idEstablecimiento)) {
            return
        }

        viewModelScope.launch {
            _operacionesEnProgreso.add(idEstablecimiento)
            _isLoading.value = true

            val idUsuario = sessionManager.getJovenId()
            if (idUsuario == null || idUsuario == -1) {
                _mensaje.value = "Sesión inválida"
                _isLoading.value = false
                _operacionesEnProgreso.remove(idEstablecimiento)
                return@launch
            }

            try {
                val resultado = if (esFavoritoActual) {
                    ServicioRemotoFavoritos.eliminarFavorito(idUsuario, idEstablecimiento)
                } else {
                    ServicioRemotoFavoritos.agregarFavorito(idUsuario, idEstablecimiento)
                }

                resultado.onSuccess { mensaje ->
                    _mensaje.value = mensaje
                    val nuevoEstado = !esFavoritoActual

                    busquedaViewModel?.actualizarFavoritoLocal(idEstablecimiento, nuevoEstado)
                    onUpdateDetalle?.invoke(nuevoEstado)

                }.onFailure { error ->
                    val mensajeError = when {
                        error.message?.contains("Ya está en favoritos", ignoreCase = true) == true ->
                            "Este establecimiento ya está en favoritos"
                        error.message?.contains("409") == true ->
                            "Este establecimiento ya está en favoritos"
                        error.message?.contains("404") == true ->
                            "Favorito no encontrado"
                        else ->
                            "Error: ${error.message}"
                    }
                    _mensaje.value = mensajeError

                    //  Revertir estado en caso de error
                    if (error.message?.contains("409") == true ||
                        error.message?.contains("Ya está en favoritos", ignoreCase = true) == true) {
                        if (!esFavoritoActual) {
                            busquedaViewModel?.actualizarFavoritoLocal(idEstablecimiento, true)
                            onUpdateDetalle?.invoke(true)
                        }
                    }
                }
            } catch (e: Exception) {
                _mensaje.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
                _operacionesEnProgreso.remove(idEstablecimiento)
            }
        }
    }

    /**
     * Limpia cualquier mensaje actual de éxito o error.
     */
    fun clearMensaje() {
        _mensaje.value = null
    }
}