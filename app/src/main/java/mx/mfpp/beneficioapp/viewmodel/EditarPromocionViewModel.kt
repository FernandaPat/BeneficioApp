package mx.mfpp.beneficioapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.network.RetrofitClient
import mx.mfpp.beneficioapp.utils.ErrorHandler
import retrofit2.HttpException

class EditarPromocionViewModel : ViewModel() {

    // 🔹 Estado de la promoción seleccionada
    var promocion = mutableStateOf<Promocion?>(null)
        private set

    // 🔹 Imagen seleccionada localmente (para mostrar previsualización)
    var nuevaImagenUri = mutableStateOf<Uri?>(null)
        private set

    // 🔹 Estado de carga y errores
    var isLoading = mutableStateOf(false)
        private set

    var error = mutableStateOf<String?>(null)
        private set

    var mensajeExito = mutableStateOf<String?>(null)
        private set


    /**
     * 🔹 Cargar una promoción por ID
     */
    fun cargarPromocionPorId(idPromocion: Int) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                Log.d("EDITAR_PROMO_DEBUG", "Cargando promoción con ID = $idPromocion")
                val response = RetrofitClient.api.obtenerPromocionPorId(idPromocion)
                promocion.value = response
            } catch (e: Exception) {
                e.printStackTrace()
                error.value = ErrorHandler.obtenerMensajeError(e)
            } finally {
                isLoading.value = false
            }
        }
    }


    /**
     * 🔹 Guardar cambios (PUT)
     */
    fun guardarCambios(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val promoActual = promocion.value ?: return

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            mensajeExito.value = null

            try {
                val response = RetrofitClient.api.actualizarPromocion(
                    promoActual.id,
                    promoActual
                )

                if (response.isSuccessful) {
                    mensajeExito.value = "Promoción actualizada con éxito 🎉"
                    onSuccess()
                } else {
                    val mensaje = "Error al actualizar: ${response.code()} ${response.message()}"
                    error.value = mensaje
                    onError(mensaje)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                val mensaje = ErrorHandler.obtenerMensajeError(e)
                error.value = mensaje
                onError(mensaje)
            } finally {
                isLoading.value = false
            }
        }
    }

    // 🔹 Métodos para actualizar campos individuales
    fun actualizarNombre(nuevo: String) {
        promocion.value = promocion.value?.copy(nombre = nuevo)
    }

    fun actualizarDescripcion(nuevo: String) {
        promocion.value = promocion.value?.copy(descripcion = nuevo)
    }

    fun actualizarDescuento(nuevo: String) {
        promocion.value = promocion.value?.copy(descuento = nuevo)
    }

    fun actualizarCategoria(nueva: String) {
        promocion.value = promocion.value?.copy(categoria = nueva)
    }

    fun actualizarExpiraEn(dias: Int?) {
        promocion.value = promocion.value?.copy(expiraEn = dias)
    }

    fun actualizarImagen(uri: Uri?) {
        nuevaImagenUri.value = uri
    }
}
