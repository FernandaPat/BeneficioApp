package mx.mfpp.beneficioapp.model


/**
 * Representa la información básica de una promoción utilizada dentro de **BeneficioApp**.
 *
 * Esta clase modela los datos asociados a una promoción aplicada por un usuario
 * o un negocio, incluyendo la tarjeta utilizada, la fecha del canje y el nombre de la promoción.
 *
 * @property numeroTarjeta Número de la tarjeta del usuario que aplicó la promoción.
 * @property fecha Fecha en la que se realizó la promoción o canje.
 * @property nombrePromocion Nombre o descripción breve de la promoción aplicada.
 */
data class PromocionData(
    val numeroTarjeta: String,
    val fecha: String,
    val nombrePromocion: String
)
