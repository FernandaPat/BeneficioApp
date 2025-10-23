/**
 * Archivo: PromocionRequest.kt
 *
 * Define el modelo de datos utilizado para enviar una solicitud
 * de creación o publicación de una nueva promoción desde un negocio.
 *
 * Este modelo se serializa en formato JSON al realizar una
 * petición POST hacia el servidor mediante Retrofit.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud para registrar una promoción.
 *
 * Contiene los campos que el servidor requiere para crear una nueva promoción
 * asociada a un negocio dentro de la aplicación.
 *
 * @property id_negocio Identificador único del negocio que publica la promoción
 * @property titulo_promocion Título o nombre visible de la promoción
 * @property descripcion Descripción breve o detallada de la promoción
 * @property descuento Valor o porcentaje del descuento (por ejemplo, "15%" o "2x1") (opcional)
 * @property categoria Categoría o tipo de promoción (por ejemplo, "Comida", "Belleza")
 * @property ubicacion Dirección o ubicación del negocio donde aplica la promoción (opcional)
 * @property fecha_expiracion Fecha de expiración o fin de validez (opcional, formato ISO: "2025-12-31")
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
