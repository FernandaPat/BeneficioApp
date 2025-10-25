package mx.mfpp.beneficioapp.utils

/**
 * Normaliza una URL de imagen (foto) para asegurar que siempre sea una URL válida y absoluta.
 *
 * 1.  Si la [foto] de entrada es nula, vacía o la cadena literal "null",
 * devuelve una URL de marcador de posición (placeholder) de "picsum.photos",
 * utilizando el [id] para asegurar una imagen aleatoria diferente.
 * 2.  Si la [foto] ya es una URL absoluta (comienza con "http"), la devuelve
 * sin cambios.
 * 3.  Si la [foto] es una ruta relativa (no comienza con "http"),
 * le antepone una URL base de Google Cloud Storage.
 *
 * @param foto La cadena [String] de la URL de la imagen (puede ser nula, vacía o una ruta parcial).
 * @param id Un identificador (como el ID de la promoción o establecimiento)
 * usado para aleatorizar la imagen de marcador de posición.
 * @return Una [String] que representa una URL de imagen absoluta y válida.
 */
fun normalizarUrlImagen(foto: String?, id: Int): String {
    // Si la foto es nula, vacía o literalmente "null", usa un placeholder
    if (foto.isNullOrBlank() || foto == "null") {
        return "https://picsum.photos/200/150?random=$id"
    }

    // Si ya es una URL completa, devuélvela
    return if (foto.startsWith("http", ignoreCase = true)) {
        foto
    } else {
        // Si es una ruta parcial (común en favoritos), añade la base de GCS
        "https://storage.googleapis.com/beneficiojoven-imagenes/${foto.trimStart('/')}"
    }
}