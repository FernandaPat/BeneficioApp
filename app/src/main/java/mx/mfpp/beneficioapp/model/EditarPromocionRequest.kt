package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para editar
 * los detalles de una promoción existente.
 *
 * @property id_promocion El identificador único de la promoción que se desea modificar.
 * @property titulo_promocion El nuevo título que tendrá la promoción.
 * @property descripcion La nueva descripción detallada de la promoción.
 * @property descuento El nuevo texto o valor del descuento (ej. "10%"), puede ser nulo.
 * @property categoria La nueva categoría a la que pertenecerá la promoción.
 * @property disponible_desde La nueva fecha (en formato String) desde la cual estará disponible. Puede ser nulo.
 * @property hasta La nueva fecha (en formato String) de vencimiento de la promoción. Puede ser nulo.
 * @property imagen La nueva URL o referencia (quizás en Base64) de la imagen. Puede ser nulo.
 */
data class EditarPromocionRequest(
    val id_promocion: Int,
    val titulo_promocion: String,
    val descripcion: String,
    val descuento: String?,
    val categoria: String,
    val disponible_desde: String?,
    val hasta: String?,
    val imagen: String?
)