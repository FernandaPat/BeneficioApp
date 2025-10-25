package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para agregar
 * una nueva promoción a un negocio específico.
 *
 * @property id_negocio El identificador único del negocio al que se asociará la promoción.
 * @property titulo El título de la nueva promoción.
 * @property descripcion La descripción detallada de la promoción.
 * @property descuento El texto o valor del descuento (ej. "15%", "2x1").
 * @property disponible_desde La fecha (en formato String) desde la cual la promoción estará disponible.
 * @property hasta La fecha (en formato String) de vencimiento de la promoción.
 * @property imagen La URL o referencia (quizás en Base64) de la imagen para la promoción.
 */
data class AgregarPromocionRequest(
    val id_negocio: Int,
    val titulo: String,
    val descripcion: String,
    val descuento: String,
    val disponible_desde: String,
    val hasta: String,
    val imagen: String
)