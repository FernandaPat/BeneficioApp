package mx.mfpp.beneficioapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.repository.FotoRepository
/**
 * ViewModel encargado de subir la foto de perfil de un joven.
 *
 * @param application Contexto de la aplicación, necesario para repositorios y sesiones.
 */
class SubirFotoJovenViewModel(application: Application) : AndroidViewModel(application) {

    /** Repositorio para operaciones con fotos */
    private val repo = FotoRepository(application.applicationContext)

    /** Administrador de sesión para obtener ID del usuario */
    private val session = SessionManager(application.applicationContext)

    /** Indica si actualmente se está subiendo la foto */
    private val _subiendo = MutableStateFlow(false)
    val subiendo = _subiendo.asStateFlow()

    /** Mensaje de estado o resultado de la operación */
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    /**
     * Sube la foto de perfil del joven.
     *
     * @param uri URI de la imagen seleccionada.
     *
     * Convierte la imagen a Base64 y la envía al servidor. Actualiza el flujo
     * [_mensaje] con el resultado de la operación.
     */
    fun subirFoto(uri: Uri) {
        viewModelScope.launch {
            try {
                _subiendo.value = true
                val idUsuario = session.getJovenId() ?: 0
                val base64 = repo.convertirUriABase64(uri)
                val exito = repo.subirFotoJoven(base64, idUsuario)

                _mensaje.value = if (exito)
                    "✅ Foto actualizada correctamente"
                else
                    "❌ Error al subir la foto"
            } catch (e: Exception) {
                e.printStackTrace()
                _mensaje.value = "⚠️ Error inesperado"
            } finally {
                _subiendo.value = false
            }
        }
    }

    /**
     * Limpia el mensaje de estado.
     *
     * Útil para reiniciar la UI después de mostrar un Toast, Snackbar o alerta.
     */
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}