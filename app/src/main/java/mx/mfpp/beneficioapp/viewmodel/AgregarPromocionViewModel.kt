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
    fun compressImageToBase64(imageFile: File, maxSizeKB: Int = 500): String {
        try {
            // 1. Leer imagen
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

            if (bitmap == null) {
                Log.e("PromocionService", "❌ No se pudo decodificar la imagen")
                return ""
            }

            Log.d("PromocionService", "📸 Imagen original: ${bitmap.width}x${bitmap.height}")

            // 2. Redimensionar si es muy grande
            val maxWidth = 1200
            val maxHeight = 1200
            val scaledBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                val ratio = Math.min(
                    maxWidth.toFloat() / bitmap.width,
                    maxHeight.toFloat() / bitmap.height
                )
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()

                Log.d("PromocionService", "🔄 Redimensionando a: ${newWidth}x${newHeight}")
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }

            // 3. Comprimir con calidad variable hasta llegar al tamaño deseado
            var quality = 90
            var base64String = ""

            do {
                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                val byteArray = outputStream.toByteArray()

                base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)

                val sizeKB = byteArray.size / 1024
                Log.d("PromocionService", "📊 Calidad: $quality%, Tamaño: ${sizeKB}KB, Base64: ${base64String.length} chars")

                if (sizeKB <= maxSizeKB || quality <= 10) {
                    break
                }

                quality -= 10
            } while (true)

            // 4. Agregar prefijo data:image
            val finalBase64 = "data:image/jpeg;base64,$base64String"

            Log.d("PromocionService", "✅ Conversión completa: ${finalBase64.length} caracteres")

            // Limpiar
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle()
            }
            bitmap.recycle()

            return finalBase64

        } catch (e: Exception) {
            Log.e("PromocionService", "❌ Error comprimiendo imagen: ${e.message}", e)
            return ""
        }
    }

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
