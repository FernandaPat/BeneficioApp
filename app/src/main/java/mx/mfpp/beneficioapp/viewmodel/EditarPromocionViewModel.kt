package mx.mfpp.beneficioapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.network.RetrofitClient
import retrofit2.HttpException
import java.io.IOException

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


    /**
     * Obtiene la promoción desde la API por su ID
     */
    fun cargarPromocionPorId(idPromocion: Int) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                Log.d("EDITAR_PROMO_DEBUG", "Cargando promoción con ID = $idPromocion")
                val response = RetrofitClient.api.obtenerPromocionPorId(idPromocion)
                promocion.value = response
            } catch (e: HttpException) {
                Log.e("EDITAR_PROMO_DEBUG", "Error HTTP ${e.code()}: ${e.message()}")
                error.value = "Error HTTP: ${e.message()}"
            } catch (e: Exception) {
                Log.e("EDITAR_PROMO_DEBUG", "Error inesperado: ${e.message}")
                error.value = "Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }


    /**
     * Actualiza una promoción (PATCH o PUT en la API)
     */
    fun guardarCambios() {
        val promoActual = promocion.value ?: return
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                RetrofitClient.api.actualizarPromocion(
                    promoActual.id,
                    promoActual
                )
            } catch (e: IOException) {
                error.value = "Error de red: ${e.message}"
            } catch (e: HttpException) {
                error.value = "Error del servidor: ${e.message}"
            } catch (e: Exception) {
                error.value = "Error desconocido: ${e.message}"
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
