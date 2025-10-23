/**
 * Archivo: AplicarPromocionRequest.kt
 *
 * Define los modelos de datos utilizados para aplicar una promoción mediante un código QR
 * o un canje directo en la aplicación.
 *
 * Incluye tanto la estructura de la solicitud enviada al servidor como la respuesta recibida.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud para aplicar una promoción.
 *
 * Contiene los identificadores necesarios para registrar el uso de una promoción
 * asociada a una tarjeta y un establecimiento.
 *
 * @property id_tarjeta Identificador de la tarjeta del usuario que realiza el canje
 * @property id_promocion Identificador de la promoción aplicada
 * @property id_establecimiento Identificador del establecimiento donde se aplica la promoción
 */
data class AplicarPromocionRequest(
    val id_tarjeta: Int,
    val id_promocion: Int,
    val id_establecimiento: Int
)
/**
 * Representa la respuesta del servidor tras aplicar una promoción.
 *
 * Contiene información sobre el resultado de la operación, el registro del canje
 * y los detalles relacionados con la promoción aplicada.
 *
 * @property success Indica si la operación fue exitosa
 * @property message Mensaje informativo devuelto por el servidor
 * @property registro Objeto [RegistroPromocion] con los datos del canje realizado
 * @property detalles Objeto [DetallesPromocion] con información complementaria
 */
data class AplicarPromocionResponse(
    val success: Boolean,
    val message: String,
    val registro: RegistroPromocion,
    val detalles: DetallesPromocion
)
/**
 * Representa el registro del uso de una promoción por parte de un usuario.
 *
 * @property id_tarjeta_promocion Identificador único del registro de la promoción usada
 * @property id_tarjeta Identificador de la tarjeta del usuario
 * @property id_promocion Identificador de la promoción aplicada
 * @property fecha_uso Fecha en la que se realizó el canje o uso de la promoción
 * @property estado Estado actual del registro (por ejemplo, "usada", "expirada")
 */
data class RegistroPromocion(
    val id_tarjeta_promocion: Int,
    val id_tarjeta: Int,
    val id_promocion: Int,
    val fecha_uso: String,
    val estado: String
)
/**
 * Contiene información detallada sobre la promoción aplicada y los participantes.
 *
 * @property joven Nombre del usuario joven que realizó el canje
 * @property promocion Nombre o título de la promoción aplicada
 * @property establecimiento Nombre del establecimiento donde se aplicó la promoción
 */
data class DetallesPromocion(
    val joven: String,
    val promocion: String,
    val establecimiento: String
)