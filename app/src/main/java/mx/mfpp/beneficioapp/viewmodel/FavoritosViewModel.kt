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

/// FavoritosViewModel.kt - VERSIÓN CORREGIDA (sin contexto)
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
            Log.d("FAVORITOS_VM", "⏳ Operación ya en progreso para $idEstablecimiento")
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
                Log.d("FAVORITOS_VM", "🔄 Iniciando toggle: $idEstablecimiento - Estado actual: $esFavoritoActual")

                val resultado = if (esFavoritoActual) {
                    Log.d("FAVORITOS_VM", "🗑️ Eliminando favorito: $idEstablecimiento")
                    ServicioRemotoFavoritos.eliminarFavorito(idUsuario, idEstablecimiento)
                } else {
                    Log.d("FAVORITOS_VM", "❤️ Agregando favorito: $idEstablecimiento")
                    ServicioRemotoFavoritos.agregarFavorito(idUsuario, idEstablecimiento)
                }

                resultado.onSuccess { mensaje ->
                    _mensaje.value = mensaje
                    Log.d("FAVORITOS_VM", "✅ $mensaje")

                    val nuevoEstado = !esFavoritoActual
                    Log.d("FAVORITOS_VM", "🎯 Nuevo estado: $nuevoEstado")

                    // 🔹 ACTUALIZAR ESTADO LOCAL INMEDIATAMENTE
                    busquedaViewModel?.actualizarFavoritoLocal(idEstablecimiento, nuevoEstado)
                    onUpdateDetalle?.invoke(nuevoEstado)

                }.onFailure { error ->
                    Log.e("FAVORITOS_VM", "❌ Error en toggle: ${error.message}", error)

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

                    // 🔹 MEJOR MANEJO DE ERROR 409
                    if (error.message?.contains("409") == true ||
                        error.message?.contains("Ya está en favoritos", ignoreCase = true) == true) {
                        // Si intentamos agregar y ya existe, forzar estado a true
                        if (!esFavoritoActual) {
                            Log.d("FAVORITOS_VM", "🔄 Forzando estado a TRUE por error 409")
                            busquedaViewModel?.actualizarFavoritoLocal(idEstablecimiento, true)
                            onUpdateDetalle?.invoke(true)
                        }
                        // 🔹 CORRECCIÓN: No podemos recargar aquí sin contexto,
                        // la recarga se hace desde la UI
                    }
                }
            } catch (e: Exception) {
                _mensaje.value = "Error de conexión: ${e.message}"
                Log.e("FAVORITOS_VM", "❌ Exception: ${e.message}", e)
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