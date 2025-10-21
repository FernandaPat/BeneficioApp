package mx.mfpp.beneficioapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.ServicioRemotoPromociones
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.utils.ImageUtils
import mx.mfpp.beneficioapp.utils.ErrorHandler
import android.util.Log
import android.view.PixelCopy.request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mx.mfpp.beneficioapp.model.ActualizarPromocionRequest
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.ServicioRemotoActualizarPromocion

class EditarPromocionViewModel(application: Application) : AndroidViewModel(application) {

    val nombre = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val descuento = mutableStateOf("")
    val imagenRemota = mutableStateOf<String?>(null)
    val nuevaImagenUri = mutableStateOf<Uri?>(null)


    private val _promociones = MutableStateFlow<List<Promocion>>(emptyList())
    val promociones: StateFlow<List<Promocion>> = _promociones

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val sessionManager = SessionManager(application)

    /**
     * Carga los datos de la promoción seleccionada por ID desde la API
     */
    fun cargarPromocionPorId(idPromocion: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val idNegocio = sessionManager.getNegocioId() ?: 0

                val response = ServicioRemotoPromociones.api.getPromocionesPorNegocio(idNegocio)
                if (response.isSuccessful) {
                    val promociones = response.body()?.data ?: emptyList()
                    val promo = promociones.find { it.id == idPromocion }

                    promo?.let {
                        nombre.value = it.titulo ?: ""
                        descripcion.value = it.descripcion ?: ""
                        descuento.value = it.descuento ?: ""
                        imagenRemota.value = it.foto
                    }

                } else {
                    _error.value = "Error ${response.code()}: ${response.message()}"
                    Log.e("EditarPromocionVM", _error.value ?: "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = ErrorHandler.obtenerMensajeError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Actualiza el texto del nombre
     */
    fun actualizarNombre(nuevo: String) {
        nombre.value = nuevo
    }

    /**
     * Actualiza la descripción
     */
    fun actualizarDescripcion(nuevo: String) {
        descripcion.value = nuevo
    }

    /**
     * Actualiza el descuento
     */
    fun actualizarDescuento(nuevo: String) {
        descuento.value = nuevo
    }

    /**
     * Actualiza la imagen seleccionada
     */
    fun actualizarImagen(uri: Uri?) {
        nuevaImagenUri.value = uri
    }

    /**
     * Lógica para actualizar la promoción con el endpoint de Cloud Run
     */
    fun actualizarPromocion(
        context: Context,
        idPromocion: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val imagenBase64 = nuevaImagenUri.value?.let {
                    ImageUtils.uriToBase64(context, it)
                } ?: ""

                val requestBody = mapOf(
                    "id_promocion" to idPromocion,
                    "titulo" to nombre.value,
                    "descripcion" to descripcion.value,
                    "descuento" to descuento.value,
                    "imagen" to imagenBase64
                )

                val request = ActualizarPromocionRequest(
                    id_promocion = idPromocion,
                    titulo = nombre.value,
                    descripcion = descripcion.value,
                    descuento = descuento.value,
                    imagen = imagenBase64
                )

                val response = ServicioRemotoActualizarPromocion.api.actualizarPromocion(request)


                if (response.isSuccessful) {
                    Log.d("ActualizarPromocion", "✅ Promoción actualizada correctamente")
                    onSuccess()
                } else {
                    onError("❌ Error ${response.code()}: ${response.message()}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                onError(ErrorHandler.obtenerMensajeError(e))
            } finally {
                _isLoading.value = false

            }
        }
    }
}
