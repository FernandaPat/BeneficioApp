package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.ServicioRemotoActualizarPromocion
import mx.mfpp.beneficioapp.model.ServicioRemotoObtenerPromocion
import java.io.InputStream
import android.util.Base64
import java.io.ByteArrayOutputStream

class EditarPromocionViewModel : ViewModel() {

    val nombre = MutableStateFlow("")
    val descripcion = MutableStateFlow("")
    val descuento = MutableStateFlow("")
    val desde = MutableStateFlow("")
    val hasta = MutableStateFlow("")
    val imagenUrl = MutableStateFlow("")
    val uri = MutableStateFlow<Uri?>(null)
    val isLoading = MutableStateFlow(false)

    /**
     * Cargar datos de la promoción a editar
     */
    fun cargarPromocion(idPromocion: Int, onLoaded: (Promocion?) -> Unit) {
        viewModelScope.launch {
            try {
                println("🟣 Cargando promoción con ID: $idPromocion")
                val promo = ServicioRemotoObtenerPromocion.obtenerPromocionPorId(idPromocion)

                promo?.let {
                    nombre.value = it.nombre
                    descripcion.value = it.descripcion
                    descuento.value = it.descuento
                    desde.value = it.desde
                    hasta.value = it.hasta
                    imagenUrl.value = it.imagenUrl
                    println("✅ Promoción cargada: ${it.nombre}")
                } ?: println("⚠️ No se encontró la promoción con ID: $idPromocion")

                onLoaded(promo)
            } catch (e: Exception) {
                println("❌ Error al cargar promoción: ${e.message}")
                onLoaded(null)
            }
        }
    }

    /**
     * Convierte un Uri (imagen) en cadena base64
     */
    private suspend fun convertirImagenABase64(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return@withContext ""
            val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            "data:image/jpeg;base64,$base64"
        } catch (e: Exception) {
            println("⚠️ Error al convertir imagen: ${e.message}")
            ""
        }
    }

    /**
     * Actualiza los datos de la promoción
     */
    fun actualizarPromocion(
        idPromocion: Int,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                println("🟣 Actualizando promoción con ID: $idPromocion")

                // Convertir la imagen a base64 si fue cambiada
                val imagenFinal = uri.value?.let { convertirImagenABase64(context, it) } ?: imagenUrl.value

                val promoActualizada = Promocion(
                    id = idPromocion,
                    nombre = nombre.value,
                    descripcion = descripcion.value,
                    descuento = descuento.value,
                    desde = if (desde.value.isNotBlank()) desde.value else "21/10/2024", // Ejemplo o valor anterior
                    hasta = if (hasta.value.isNotBlank()) hasta.value else "30/11/2024",
                    imagenUrl = imagenUrl.value
                )


                val exito = ServicioRemotoActualizarPromocion.actualizarPromocion(
                    idPromocion = idPromocion,
                    promocion = promoActualizada
                )

                if (exito) {
                    onSuccess()
                } else {
                    onError("No has modificado ningun dato")
                }

            } catch (e: Exception) {
                onError("Excepción al actualizar promoción: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
    fun onNuevaImagen(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val outputStream = ByteArrayOutputStream()

                    // 🔹 Compresión: ajusta la calidad según lo necesario (80 → buena calidad)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    val compressedBytes = outputStream.toByteArray()
                    outputStream.close()

                    // 🔹 Convertimos la imagen comprimida a Base64
                    val base64String = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
                    val base64Final = "data:image/jpeg;base64,$base64String"

                    imagenUrl.value = base64Final
                    this@EditarPromocionViewModel.uri.value = uri

                    println("✅ Imagen comprimida y convertida (${compressedBytes.size / 1024} KB)")
                } else {
                    println("⚠️ No se pudo decodificar la imagen seleccionada")
                }
            } catch (e: Exception) {
                println("❌ Error al comprimir/convertir imagen: ${e.localizedMessage}")
            }
        }
    }


}
