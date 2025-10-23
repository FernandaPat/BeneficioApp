/**
 * Archivo: ValidarQRRequest.kt
 *
 * Define los modelos de datos utilizados para la validación de códigos QR
 * dentro del sistema de promociones.
 *
 * Incluye la estructura de la solicitud, la respuesta del servidor y
 * los objetos anidados que contienen la información del QR validado.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud para validar un código QR.
 *
 * Contiene los datos necesarios para que el servidor verifique la validez
 * del token generado y la relación con un establecimiento.
 *
 * @property token Código único generado previamente al crear el QR
 * @property id_establecimiento Identificador del establecimiento que realiza la validación
 */
data class ValidarQRRequest(
    val token: String,
    val id_establecimiento: Int
)
/**
 * Representa la respuesta del servidor al validar un código QR.
 *
 * Indica el resultado de la validación y, en caso exitoso, incluye los datos
 * asociados al usuario, promoción y token.
 *
 * @property success Indica si la validación fue exitosa
 * @property message Mensaje descriptivo del resultado
 * @property datos Objeto [QRDatos] con la información del QR validado (opcional)
 */
data class QRValidationResponse(
    val success: Boolean,
    val message: String,
    val datos: QRDatos? = null
)
/**
 * Contiene los datos completos asociados a un código QR validado.
 *
 * Incluye la información del usuario joven, la promoción asociada y
 * los detalles del token generado.
 *
 * @property joven Objeto [JovenValidarScanner] con los datos del usuario joven
 * @property promocion Objeto [PromocionValidarScanner] con los datos de la promoción
 * @property token_info Objeto [TokenInfo] con la información del token generado
 */
data class QRDatos(
    val joven: JovenValidarScanner,
    val promocion: PromocionValidarScanner,
    val token_info: TokenInfo
)
/**
 * Representa la información del usuario joven obtenida al validar el código QR.
 *
 * @property id_joven Identificador del usuario joven
 * @property nombre Nombre completo del usuario
 * @property curp CURP del usuario (opcional)
 * @property id_tarjeta Identificador de la tarjeta asociada al usuario
 * @property folio_digital Folio digital único asignado al usuario
 */
data class JovenValidarScanner(
    val id_joven: Int,
    val nombre: String,
    val curp: String?,
    val id_tarjeta: Int,
    val folio_digital: String
)
/**
 * Representa la información de la promoción asociada a un código QR validado.
 *
 * @property id_promocion Identificador único de la promoción
 * @property nombre Nombre o título de la promoción
 * @property descripcion Descripción detallada de la promoción (opcional)
 * @property establecimiento Nombre del establecimiento donde se valida el QR
 */
data class PromocionValidarScanner(
    val id_promocion: Int,
    val nombre: String,
    val descripcion: String?,
    val establecimiento: String
)
/**
 * Contiene la información técnica del token QR validado.
 *
 * @property generado Fecha y hora en la que el token fue generado
 * @property expira_timestamp Marca de tiempo (timestamp) UNIX en la que expira el token
 */
data class TokenInfo(
    val generado: String,
    val expira_timestamp: Long
)

