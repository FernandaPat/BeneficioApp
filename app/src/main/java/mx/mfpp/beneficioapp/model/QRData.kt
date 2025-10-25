package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos para la solicitud (request) de generación de un código QR.
 *
 * @property id_joven El ID del usuario (joven) que solicita el QR.
 * @property id_promocion El ID de la promoción para la cual se solicita el QR.
 */
data class GenerarQRRequest(
    val id_joven: Int,
    val id_promocion: Int
)

/**
 * Modelo de datos que contiene la información de vista previa (preview)
 * asociada a un token QR. Se usa dentro de [QRTokenResponse].
 *
 * @property joven El nombre del usuario (joven) asociado al QR.
 * @property folio_digital El folio digital del usuario.
 * @property promocion El título de la promoción asociada.
 * @property descripcion La descripción de la promoción.
 * @property establecimiento El nombre del establecimiento donde aplica.
 */
data class QRPreview(
    val joven: String,
    val folio_digital: String,
    val promocion: String,
    val descripcion: String,
    val establecimiento: String
)

/**
 * Modelo de datos para la respuesta (response) del servidor al generar un token QR.
 *
 * @property success Indica (true/false) si la generación del token fue exitosa.
 * @property token El token (generalmente JWT o una cadena única) que se codificará en el QR.
 * @property expira_en Una cadena descriptiva del tiempo de expiración (ej. "10 minutos").
 * @property expira_timestamp El timestamp (Unix time) exacto de la expiración.
 * @property preview Un objeto [QRPreview] con los detalles para mostrar al usuario.
 */
data class QRTokenResponse(
    val success: Boolean,
    val token: String,
    val expira_en: String,
    val expira_timestamp: Long,
    val preview: QRPreview
)