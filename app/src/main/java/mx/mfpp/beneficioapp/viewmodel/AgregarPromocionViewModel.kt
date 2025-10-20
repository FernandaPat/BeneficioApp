package mx.mfpp.beneficioapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.PromocionRequest
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.network.RetrofitClient
import mx.mfpp.beneficioapp.utils.ErrorHandler
import java.io.ByteArrayOutputStream
import java.io.File

class AgregarPromocionViewModel(application: Application) : AndroidViewModel(application) {

    // Campos de la UI
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
     * Envía la promoción a la API con ID dinámico del negocio logueado.
     */


    fun guardarPromocion(
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

                // ⚠️ En producción deberías convertir la imagen URI a Base64 real
                val imagenBase64 =
                    "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/..." // temporal

                val promocion = PromocionRequest(
                    id_negocio = idNegocio,
                    titulo = nombre.value.ifBlank { "" },
                    descripcion = descripcion.value.ifBlank { "" },
                    descuento = descuento.value.ifBlank { null }, // este puede ser null
                    disponible_desde = desde.value.ifBlank { "" },
                    hasta = hasta.value.ifBlank { "" },
                    imagen = imagenBase64.ifBlank { "" }
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
