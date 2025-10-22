package mx.mfpp.beneficioapp.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.InputStream

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
