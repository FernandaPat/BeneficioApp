package mx.mfpp.beneficioapp.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.InputStream

/**
 * Lee una imagen desde una [Uri] y la convierte en una cadena de texto Base64.
 *
 * La cadena resultante incluye el prefijo MIME type "data:image/jpeg;base64,".
 *
 * @param context El [Context] de la aplicación, necesario para usar
 * el `ContentResolver` y abrir el stream de la [Uri].
 * @param uri La [Uri] del archivo de imagen que se va a convertir.
 * @return La [String] en formato Base64 con el prefijo, o una cadena vacía
 * si los bytes no se pudieron leer (ej. URI inválida).
 */
fun convertirImagenABase64(context: Context, uri: Uri): String {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes()
    inputStream?.close()
    return if (bytes != null) {
        "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
    } else {
        ""
    }
}