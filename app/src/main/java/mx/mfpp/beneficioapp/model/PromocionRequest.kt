package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para crear
 * o definir una nueva promoción por parte de un negocio.
 *
 * @property id_negocio El identificador único del negocio que publica la promoción.
 * @property titulo_promocion El título o nombre descriptivo de la promoción.
 * @property descripcion Una descripción breve de en qué consiste la promoción.
 * @property descuento El texto del descuento (ej. "15%", "2x1"). Puede ser nulo.
 * @property categoria La categoría a la que pertenece la promoción (ej. "Comida", "Belleza").
 * @property ubicacion La dirección o ubicación física donde aplica (opcional).
 * @property fecha_expiracion La fecha de expiración en formato String (ej. "2025-12-31"). Puede ser nulo.
 */
data class PromocionRequest(
    val id_negocio: Int,
    val titulo_promocion: String,
    val descripcion: String,
    val descuento: String?,
    val categoria: String,
    val ubicacion: String?,
    val fecha_expiracion: String?
)
