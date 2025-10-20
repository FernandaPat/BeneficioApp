package mx.mfpp.beneficioapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.AgregarPromocionRequest
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.network.RetrofitClient
import mx.mfpp.beneficioapp.utils.ErrorHandler
import mx.mfpp.beneficioapp.utils.ImageUtils // ✅ importa tu util de conversión Base64

class AgregarPromocionViewModel(application: Application) : AndroidViewModel(application) {

    // Campos del formulario
    val uri = mutableStateOf<Uri?>(null)
    val nombre = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val descuento = mutableStateOf("")
    val categoria = mutableStateOf("")
    val desde = mutableStateOf("")
    val hasta = mutableStateOf("")
    val expiraEn = mutableStateOf<Int?>(null)
    val categorias = listOf("Alimentos", "Ropa", "Entretenimiento", "Servicios", "Salud")

    private val sessionManager = SessionManager(application)

    /**
     * Envía la promoción a la API con el ID del negocio logueado y la imagen en Base64 real.
     */
    fun guardarPromocion(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val idNegocio = sessionManager.getNegocioId()
                if (idNegocio == null || idNegocio <= 0) {
                    onError("No se encontró el ID del negocio en la sesión.")
                    return@launch
                }

                // ✅ Convierte la imagen seleccionada (si existe) a Base64 real
                val imagenBase64 = uri.value?.let { ImageUtils.uriToBase64(context, it) } ?: ""

                val promocion = AgregarPromocionRequest(
                    id_negocio = idNegocio,
                    titulo = nombre.value.ifBlank { "" },
                    descripcion = descripcion.value.ifBlank { "" },
                    descuento = descuento.value.ifBlank { null },
                    disponible_desde = desde.value.ifBlank { "" },
                    hasta = hasta.value.ifBlank { "" },
                    imagen = imagenBase64
                )

                Log.d("API_REQUEST", "Enviando promoción: $promocion")

                val response = RetrofitClient.api.registrarPromocion(promocion)
                if (response.isSuccessful) {
                    Log.d("API_SUCCESS", "Promoción agregada correctamente")
                    onSuccess()
                } else {
                    val errorMsg = "Error al registrar promoción: ${response.code()} ${response.message()}"
                    Log.e("API_ERROR", errorMsg)
                    onError(errorMsg)
                }

            } catch (e: Exception) {
                Log.e("API_EXCEPTION", e.message ?: "Error desconocido")
                val mensaje = ErrorHandler.obtenerMensajeError(e)
                onError(mensaje)
            }
        }
    }
}
