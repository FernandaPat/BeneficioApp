package mx.mfpp.beneficioapp.model

data class Promocion(
    val id: Int,
    val nombre: String,
    val imagenUrl: String?,
    val descuento: String?,
    val categoria: String,
    val expiraEn: Int?,
    val ubicacion: String?,
    val esFavorito: Boolean,
    val rating: Double?,
    val descripcion: String?

) {
    fun obtenerTextoExpiracion(): String {
        return when (expiraEn) {
            null -> "Sin fecha"
            0 -> "Expira hoy"
            1 -> "Expira mañana"
            in 2..7 -> "Expira en $expiraEn días"
            else -> " Válido por $expiraEn días"
        }
    }

    fun obtenerRatingTexto(): String {
        return rating?.let { "⭐ ${"%.1f".format(it)}" } ?: "⭐ --"
    }
}
