/**
 * Archivo: GenerarQRRequest.kt
 *
 * Define los modelos de datos relacionados con la generación de códigos QR
 * para el canje o visualización de promociones.
 *
 * Incluye tanto la estructura de la solicitud como la respuesta y el preview
 * devueltos por el servidor.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud para generar un código QR.
 *
 * Contiene los identificadores necesarios para crear un QR asociado a una promoción específica
 * y a un usuario joven.
 *
 * @property id_joven Identificador del usuario joven que solicita la generación del QR
 * @property id_promocion Identificador de la promoción asociada al QR
 */
data class GenerarQRRequest(
    val id_joven: Int,
    val id_promocion: Int
)
/**
 * Representa una vista previa (preview) del código QR generado.
 *
 * Contiene la información básica que se muestra antes de aplicar o validar el QR,
 * incluyendo datos del joven, promoción y establecimiento.
 *
 * @property joven Nombre del usuario joven
 * @property folio_digital Folio digital único asociado al usuario
 * @property promocion Nombre de la promoción
 * @property descripcion Descripción de la promoción
 * @property establecimiento Nombre del establecimiento donde aplica la promoción
 */
data class QRPreview(
    val joven: String,
    val folio_digital: String,
    val promocion: String,
    val descripcion: String,
    val establecimiento: String
)
/**
 * Representa la respuesta completa del servidor tras generar un código QR.
 *
 * Incluye el token generado, la fecha de expiración y la información de vista previa.
 *
 * @property success Indica si la generación del QR fue exitosa
 * @property token Token único del código QR generado
 * @property expira_en Fecha en la que el QR dejará de ser válido
 * @property expira_timestamp Marca de tiempo (timestamp) UNIX de expiración
 * @property preview Objeto [QRPreview] con la información visual del QR generado
 */
data class QRTokenResponse(
    val success: Boolean,
    val token: String,
    val expira_en: String,
    val expira_timestamp: Long,
    val preview: QRPreview
)