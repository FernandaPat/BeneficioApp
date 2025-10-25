package mx.mfpp.beneficioapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Objeto (Singleton) que proporciona funciones de utilidad
 * para el manejo de imágenes.
 */
object ImageUtils {

    /**
     * Convierte una [Uri] de imagen (de la galería o cámara) en una cadena
     * de texto Base64, incluyendo el prefijo MIME type.
     *
     * Utiliza [ImageDecoder] para Android P (API 28) y superior, y el
     * método obsoleto [MediaStore.Images.Media.getBitmap] para versiones anteriores.
     *
     * La imagen se comprime como JPEG con calidad del 90%.
     *
     * @param context El [Context] de la aplicación, necesario para usar el `ContentResolver`.
     * @param uri La [Uri] de la imagen que se va a convertir.
     * @return Una [String] que contiene la imagen en formato Base64 con el
     * prefijo "data:image/jpeg;base64,", o `null` si ocurre
     * una excepción durante la decodificación o compresión.
     */
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Método moderno para API 28+
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                // Método obsoleto para API < 28
                @Suppress("DEPRECATION")
                android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            val outputStream = ByteArrayOutputStream()
            // Comprime el bitmap a JPEG con 90% de calidad
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val imageBytes = outputStream.toByteArray()
            // Codifica los bytes a Base64 sin saltos de línea
            val base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            // Añade el prefijo estándar MIME type
            "data:image/jpeg;base64,$base64String"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}