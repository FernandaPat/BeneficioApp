/**
 * Archivo: AgregarPromocionRequest.kt
 *
 * Define el modelo de datos utilizado para enviar una solicitud
 * de creación de una nueva promoción en el servidor.
 *
 * Este modelo se serializa a formato JSON al realizar una
 * petición POST desde el cliente (por ejemplo, mediante Retrofit).
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud para agregar una promoción.
 *
 * Contiene los campos necesarios que el servidor requiere para registrar una nueva promoción.
 *
 * @property id_negocio Identificador único del negocio que crea la promoción
 * @property titulo Título de la promoción
 * @property descripcion Descripción detallada de la promoción
 * @property descuento Porcentaje o valor de descuento ofrecido (por ejemplo, "20%")
 * @property disponible_desde Fecha en la que la promoción comienza a estar disponible (formato dd/MM/yyyy)
 * @property hasta Fecha en la que la promoción deja de estar disponible (formato dd/MM/yyyy)
 * @property imagen URL o cadena codificada en Base64 que representa la imagen de la promoción
 */
data class AgregarPromocionRequest(
    val id_negocio: Int,
    val titulo: String,
    val descripcion: String,
    val descuento: String,
    val disponible_desde: String,
    val hasta: String,
    val imagen: String
)
