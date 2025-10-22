package mx.mfpp.beneficioapp.utils

fun normalizarUrlImagen(foto: String?, id: Int): String {
    if (foto.isNullOrBlank() || foto == "null") {
        return "https://picsum.photos/200/150?random=$id"
    }

    return if (foto.startsWith("http", ignoreCase = true)) {
        foto
    } else {
        // Para fotos que vienen con rutas parciales (como en favoritos)
        "https://storage.googleapis.com/beneficiojoven-imagenes/${foto.trimStart('/')}"
    }
}