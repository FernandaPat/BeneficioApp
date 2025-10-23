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

class SubirFotoJovenViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = FotoRepository(application.applicationContext)
    private val session = SessionManager(application.applicationContext)

    private val _subiendo = MutableStateFlow(false)
    val subiendo = _subiendo.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

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

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
