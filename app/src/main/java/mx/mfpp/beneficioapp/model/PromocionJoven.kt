
/**
 * Archivo: PromocionJoven.kt
 *
 * Define los modelos de datos relacionados con las promociones disponibles
 * para los usuarios jóvenes dentro de la aplicación.
 *
 * Incluye la estructura principal de la promoción y la respuesta del servidor
 * con paginación de resultados.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa una promoción visible o disponible para los usuarios jóvenes.
 *
 * Contiene la información principal de la promoción, su estado y los datos
 * del establecimiento que la ofrece.
 *
 * @property id Identificador único de la promoción
 * @property titulo Título o nombre descriptivo de la promoción
 * @property descripcion Descripción detallada de la promoción
 * @property fecha_creacion Fecha en la que se creó la promoción
 * @property fecha_expiracion Fecha de expiración o fin de validez de la promoción
 * @property foto URL de la imagen asociada a la promoción (opcional)
 * @property estado Estado actual de la promoción (por ejemplo, "Activa", "Expirada")
 * @property id_establecimiento Identificador del establecimiento que ofrece la promoción
 * @property nombre_establecimiento Nombre del establecimiento asociado
 * @property descuento Valor o porcentaje del descuento ofrecido (por ejemplo, "15%")
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
 * Representa la respuesta del servidor que contiene la lista de promociones disponibles
 * para los usuarios jóvenes, junto con la información de paginación.
 *
 * @property data Lista de promociones disponibles
 * @property pagination Objeto [Pagination] con los metadatos de la respuesta paginada
 */
data class PromocionesJovenResponse(
    val data: List<PromocionJoven>,
    val pagination: Pagination
)
