package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para actualizar
 * los detalles de una promoción existente.
 *
 * @property id_promocion El identificador único de la promoción que se va a modificar.
 * @property titulo El nuevo título que tendrá la promoción.
 * @property descripcion La nueva descripción detallada de la promoción.
 * @property descuento El nuevo texto o valor del descuento (ej. "20%", "2x1").
 * @property imagen La URL o referencia (quizás en Base64) de la nueva imagen para la promoción.
 */
data class ActualizarPromocionRequest(
    val id_promocion: Int,
    val titulo: String,
    val descripcion: String,
    val descuento: String,
    val imagen: String
)