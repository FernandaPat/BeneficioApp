package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.ServicioRemotoActualizarPromocion
import mx.mfpp.beneficioapp.model.ServicioRemotoObtenerPromocion
import java.io.ByteArrayOutputStream
/**
 * ViewModel para editar una promoción existente.
 *
 * Maneja la carga de datos desde el servidor, actualización de campos,
 * conversión de imágenes a Base64 y envío de cambios al backend.
 */
class EditarPromocionViewModel : ViewModel() {

    /** Nombre de la promoción */
    val nombre = MutableStateFlow("")

    /** Descripción de la promoción */
    val descripcion = MutableStateFlow("")

    /** Valor del descuento de la promoción */
    val descuento = MutableStateFlow("")

    /** Fecha de inicio de la promoción (formato dd/MM/yyyy) */
    val desde = MutableStateFlow("")

    /** Fecha de fin de la promoción (formato dd/MM/yyyy) */
    val hasta = MutableStateFlow("")

    /** URL de la imagen de la promoción (puede ser Base64) */
    val imagenUrl = MutableStateFlow("")

    /** URI de la imagen seleccionada desde el dispositivo */
    val uri = MutableStateFlow<Uri?>(null)

    /** Indica si se está realizando alguna operación de carga/actualización */
    val isLoading = MutableStateFlow(false)

    /**
     * Carga los datos de una promoción desde el servidor.
     *
     * @param idPromocion ID de la promoción a cargar.
     * @param onLoaded Callback que devuelve la promoción cargada, o null si hubo error.
     */
    fun cargarPromocion(idPromocion: Int, onLoaded: (Promocion?) -> Unit) {
        viewModelScope.launch {
            try {
                val promo = withContext(Dispatchers.IO) {
                    ServicioRemotoObtenerPromocion.obtenerPromocionPorId(idPromocion)
                }

                promo?.let {
                    nombre.value = it.nombre
                    descripcion.value = it.descripcion
                    descuento.value = it.descuento

                    // 🔹 Limpiamos y convertimos fechas si es necesario
                    val fechaDesde = it.desde.trim()
                    val fechaHasta = it.hasta.trim()
                    desde.value = formatearFecha(fechaDesde)
                    hasta.value = formatearFecha(fechaHasta)

                    imagenUrl.value = it.imagenUrl
                }

                onLoaded(promo)
            } catch (e: Exception) {
                println("❌ Error al cargar promoción: ${e.localizedMessage}")
                onLoaded(null)
            }
        }
    }

    /**
     * Convierte una nueva imagen seleccionada a Base64 comprimido
     * y actualiza los estados [imagenUrl] y [uri].
     *
     * @param uri URI de la imagen seleccionada.
     * @param context Contexto necesario para acceder al ContentResolver.
     */
    fun onNuevaImagen(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    val compressedBytes = outputStream.toByteArray()
                    outputStream.close()

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

    /**
     * Envía los cambios de la promoción al backend.
     *
     * @param idPromocion ID de la promoción que se actualizará.
     * @param context Contexto necesario para algunas operaciones.
     * @param onSuccess Lambda que se ejecuta si la actualización fue exitosa.
     * @param onError Lambda que devuelve un mensaje de error si la actualización falla.
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

                val promoActualizada = Promocion(
                    id = idPromocion,
                    nombre = nombre.value,
                    descripcion = descripcion.value,
                    descuento = descuento.value,
                    // ✅ Ya no convertimos: el backend quiere "dd/MM/yyyy"
                    desde = desde.value.trim(),
                    hasta = hasta.value.trim(),
                    imagenUrl = imagenUrl.value
                )

                val exito = withContext(Dispatchers.IO) {
                    ServicioRemotoActualizarPromocion.actualizarPromocion(
                        idPromocion = idPromocion,
                        promocion = promoActualizada
                    )
                }

                if (exito) {
                    onSuccess()
                } else {
                    onError("No has modificado ningun dato")
                }
            } catch (e: Exception) {
                onError("❌ Excepción al actualizar promoción: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * Formatea una fecha del formato "yyyy-MM-dd" a "dd/MM/yyyy".
     *
     * @param fecha Fecha en formato ISO o cadena.
     * @return Fecha formateada para mostrar en UI.
     */
    private fun formatearFecha(fecha: String): String {
        if (fecha.isBlank() || fecha.equals("null", ignoreCase = true)) return ""
        return if (fecha.contains("-")) {
            try {
                val partes = fecha.split("-")
                "${partes[2]}/${partes[1]}/${partes[0]}"
            } catch (e: Exception) {
                fecha
            }
        } else fecha
    }

    /**
     * Convierte una fecha del formato "dd/MM/yyyy" a "yyyy-MM-dd" para guardar en la base de datos.
     *
     * @param fecha Fecha en formato dd/MM/yyyy.
     * @return Fecha en formato ISO para almacenamiento.
     */
    private fun aIso(fecha: String): String {
        val partes = fecha.split("/")
        return if (partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else fecha
    }
}