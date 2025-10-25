package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa una promoción desde la perspectiva
 * de un usuario (joven), incluyendo detalles del establecimiento.
 *
 * @property id El identificador único de la promoción.
 * @property titulo El título de la promoción.
 * @property descripcion La descripción detallada de la promoción.
 * @property fecha_creacion La fecha (en formato String) en que se creó la promoción.
 * @property fecha_expiracion La fecha (en formato String) en que expira la promoción.
 * @property foto La URL de la imagen de la promoción (puede ser nula).
 * @property estado El estado actual de la promoción (ej. "Activa", "Expirada").
 * @property id_establecimiento El ID del establecimiento que ofrece la promoción.
 * @property nombre_establecimiento El nombre del establecimiento que ofrece la promoción.
 * @property descuento El texto o valor del descuento (ej. "15%").
 */
data class PromocionJoven(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val fecha_creacion: String,
    val fecha_expiracion: String,
    val foto: String?,
    val estado: String,
    val id_establecimiento: Int,
    val nombre_establecimiento: String,
    val descuento: String
)

/**
 * Modelo de datos que representa la respuesta (response) del servidor
 * al solicitar la lista de promociones para jóvenes, incluyendo paginación.
 *
 * @property data La lista de [PromocionJoven] obtenidas en la página actual.
 * @property pagination El objeto [Pagination] que contiene la información
 * de paginación (página actual, total de páginas, etc.).
 */
data class PromocionesJovenResponse(
    val data: List<PromocionJoven>,
    val pagination: Pagination
)