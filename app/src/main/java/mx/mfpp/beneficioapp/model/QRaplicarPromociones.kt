package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para aplicar
 * o canjear una promoción específica.
 *
 * @property id_tarjeta El ID de la tarjeta del usuario que realiza el canje.
 * @property id_promocion El ID de la promoción que se está canjeando.
 * @property id_establecimiento El ID del establecimiento donde se realiza el canje.
 */
data class AplicarPromocionRequest(
    val id_tarjeta: Int,
    val id_promocion: Int,
    val id_establecimiento: Int
)

/**
 * Modelo de datos para la respuesta (response) del endpoint 'aplicar-promocion'.
 * Confirma el resultado del canje.
 *
 * @property success Indica (true/false) si la promoción se aplicó exitosamente.
 * @property message Un mensaje descriptivo del resultado (ej. "Canje exitoso").
 * @property registro El objeto [RegistroPromocion] que detalla el canje realizado.
 * @property detalles El objeto [DetallesPromocion] con nombres descriptivos
 * del joven, promoción y establecimiento.
 */
data class AplicarPromocionResponse(
    val success: Boolean,
    val message: String,
    val registro: RegistroPromocion,
    val detalles: DetallesPromocion
)

/**
 * Modelo de datos que detalla el registro específico del canje
 * (el "ticket" o comprobante del canje).
 *
 * @property id_tarjeta_promocion El ID único de este registro de canje.
 * @property id_tarjeta El ID de la tarjeta utilizada.
 * @property id_promocion El ID de la promoción utilizada.
 * @property fecha_uso La fecha y hora (en formato String) en que se realizó el canje.
 * @property estado El estado final del canje (ej. "Aplicado").
 */
data class RegistroPromocion(
    val id_tarjeta_promocion: Int,
    val id_tarjeta: Int,
    val id_promocion: Int,
    val fecha_uso: String,
    val estado: String
)

/**
 * Modelo de datos que proporciona detalles descriptivos (nombres)
 * de las entidades involucradas en el canje.
 *
 * @property joven El nombre del usuario (joven) que realizó el canje.
 * @property promocion El título o nombre de la promoción canjeada.
 * @property establecimiento El nombre del establecimiento donde se realizó el canje.
 */
data class DetallesPromocion(
    val joven: String,
    val promocion: String,
    val establecimiento: String
)