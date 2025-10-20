package mx.mfpp.beneficioapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val imageBytes = outputStream.toByteArray()
            val base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            // prefijo estándar para indicar formato MIME
            "data:image/jpeg;base64,$base64String"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
