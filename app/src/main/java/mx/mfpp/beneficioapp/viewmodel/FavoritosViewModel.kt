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

class FavoritosViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _operacionesEnProgreso = mutableSetOf<Int>()

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

                    // 🔹 REVERTIR ESTADO EN CASO DE ERROR
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

    fun clearMensaje() {
        _mensaje.value = null
    }
}