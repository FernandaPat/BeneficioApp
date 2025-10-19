// FavoritosViewModel.kt
package mx.mfpp.beneficioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.ServicioRemotoFavoritos
import mx.mfpp.beneficioapp.model.SessionManager

// 🔹 CAMBIO: Hereda de ViewModel (NO AndroidViewModel)
class FavoritosViewModel(
    private val sessionManager: SessionManager  // 🔹 Recibe SessionManager
) : ViewModel() {  // 🔹 ViewModel, NO AndroidViewModel

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun toggleFavorito(idEstablecimiento: Int, esFavoritoActual: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true

            val idUsuario = sessionManager.getJovenId()
            if (idUsuario == null || idUsuario == -1) {
                _mensaje.value = "Sesión inválida"
                _isLoading.value = false
                return@launch
            }

            // 🔹 MEJORA: Verificar estado actual antes de hacer la operación
            val resultado = if (esFavoritoActual) {
                ServicioRemotoFavoritos.eliminarFavorito(idUsuario, idEstablecimiento)
            } else {
                ServicioRemotoFavoritos.agregarFavorito(idUsuario, idEstablecimiento)
            }

            resultado.onSuccess { mensaje ->
                _mensaje.value = mensaje
                Log.d("FAVORITOS_VM", "✅ $mensaje")
            }.onFailure { error ->
                // 🔹 MEJORA: Mensaje más amigable
                val mensajeError = when {
                    error.message?.contains("409") == true ->
                        "Este establecimiento ya está en favoritos"
                    error.message?.contains("404") == true ->
                        "Favorito no encontrado"
                    else ->
                        "Error: ${error.message}"
                }
                _mensaje.value = mensajeError
                Log.e("FAVORITOS_VM", "❌ ${error.message}", error)
            }

            _isLoading.value = false
        }
    }

    fun agregarFavorito(idEstablecimiento: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            val idUsuario = sessionManager.getJovenId()
            if (idUsuario == null || idUsuario == -1) {
                _mensaje.value = "Sesión inválida"
                _isLoading.value = false
                return@launch
            }

            val resultado = ServicioRemotoFavoritos.agregarFavorito(idUsuario, idEstablecimiento)

            resultado.onSuccess { mensaje ->
                _mensaje.value = mensaje
                Log.d("FAVORITOS_VM", "✅ $mensaje")
            }.onFailure { error ->
                _mensaje.value = "Error: ${error.message}"
                Log.e("FAVORITOS_VM", "❌ ${error.message}", error)
            }

            _isLoading.value = false
        }
    }

    fun eliminarFavorito(idEstablecimiento: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            val idUsuario = sessionManager.getJovenId()
            if (idUsuario == null || idUsuario == -1) {
                _mensaje.value = "Sesión inválida"
                _isLoading.value = false
                return@launch
            }

            val resultado = ServicioRemotoFavoritos.eliminarFavorito(idUsuario, idEstablecimiento)

            resultado.onSuccess { mensaje ->
                _mensaje.value = mensaje
                Log.d("FAVORITOS_VM", "✅ $mensaje")
            }.onFailure { error ->
                _mensaje.value = "Error: ${error.message}"
                Log.e("FAVORITOS_VM", "❌ ${error.message}", error)
            }

            _isLoading.value = false
        }
    }

    fun clearMensaje() {
        _mensaje.value = null
    }
}