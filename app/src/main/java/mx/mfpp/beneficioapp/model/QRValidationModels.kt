package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos para la solicitud (request) de validación de un token QR
 * por parte de un establecimiento.
 *
 * @property token El token (obtenido del QR) que se va a validar.
 * @property id_establecimiento El ID del establecimiento que está realizando la validación.
 */
data class ValidarQRRequest(
    val token: String,
    val id_establecimiento: Int
)

/**
 * Modelo de datos para la respuesta (response) del servidor al validar un token QR.
 *
 * @property success Indica (true/false) si el token es válido y la validación fue exitosa.
 * @property message Un mensaje descriptivo (ej. "Token válido" o "Token expirado").
 * @property datos El objeto [QRDatos] que contiene la información detallada
 * del token (nulo si la validación falla).
 */
data class QRValidationResponse(
    val success: Boolean,
    val message: String,
    val datos: QRDatos? = null
)

/**
 * Contiene los datos detallados extraídos del token QR validado.
 * Se utiliza dentro de [QRValidationResponse].
 *
 * @property joven Los datos del [JovenValidarScanner] (usuario) asociado al token.
 * @property promocion Los datos de la [PromocionValidarScanner] asociada al token.
 * @property token_info Información meta del [TokenInfo], como su expiración.
 */
data class QRDatos(
    val joven: JovenValidarScanner,
    val promocion: PromocionValidarScanner,
    val token_info: TokenInfo
)

/**
 * Modelo de datos con la información del usuario (joven) necesaria
 * para el scanner de validación (usado en [QRDatos]).
 *
 * @property id_joven El ID único del usuario.
 * @property nombre El nombre completo del usuario.
 * @property curp La CURP del usuario (puede ser nula).
 * @property id_tarjeta El ID de la tarjeta principal del usuario.
 * @property folio_digital El folio digital de la tarjeta del usuario.
 */
data class JovenValidarScanner(
    val id_joven: Int,
    val nombre: String,
    val curp: String?,
    val id_tarjeta: Int,
    val folio_digital: String
)

/**
 * Modelo de datos con la información de la promoción necesaria
 * para el scanner de validación (usado en [QRDatos]).
 *
 * @property id_promocion El ID único de la promoción.
 * @property nombre El título o nombre de la promoción.
 * @property descripcion La descripción de la promoción (puede ser nula).
 * @property establecimiento El nombre del establecimiento que emitió la promoción.
 */
data class PromocionValidarScanner(
    val id_promocion: Int,
    val nombre: String,
    val descripcion: String?,
    val establecimiento: String
)

/**
 * Metadatos sobre el token QR (usado en [QRDatos]).
 *
 * @property generado La fecha/hora (en formato String) en que se generó el token.
 * @property expira_timestamp El timestamp (Unix time) exacto de la expiración.
 */
data class TokenInfo(
    val generado: String,
    val expira_timestamp: Long
)