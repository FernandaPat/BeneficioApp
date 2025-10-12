package mx.mfpp.beneficioapp.model

/**
 * Representa una promoción o oferta disponible en la aplicación.
 *
 * Contiene información sobre descuentos, categorías, fechas de expiración
 * y detalles de ubicación y valoración.
 *
 * @property id Identificador único de la promoción
 * @property nombre Nombre descriptivo de la promoción
 * @property imagenUrl URL opcional de la imagen representativa
 * @property descuento Texto que describe el descuento o oferta
 * @property categoria Categoría a la que pertenece la promoción
 * @property expiraEn Número de días hasta que expire la promoción
 * @property ubicacion Ubicación física o descripción de la ubicación
 * @property esFavorito Indica si la promoción está marcada como favorita
 * @property rating Valoración numérica de la promoción (opcional)
 * @property descripcion Descripción detallada de la promoción
 */
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
    /**
     * Genera un texto legible que describe el tiempo de expiración.
     *
     * @return Texto descriptivo del estado de expiración
     */
    fun obtenerTextoExpiracion(): String {
        return when (expiraEn) {
            null -> "Sin fecha"
            0 -> "Expira hoy"
            1 -> "Expira mañana"
            in 2..7 -> "Expira en $expiraEn días"
            else -> " Válido por $expiraEn días"
        }
    }

    /**
     * Genera un texto formateado con la valoración en formato de estrellas.
     *
     * @return Texto con icono de estrella y valoración numérica
     */
    fun obtenerRatingTexto(): String {
        return rating?.let { "⭐ ${"%.1f".format(it)}" } ?: "⭐ --"
    }
}