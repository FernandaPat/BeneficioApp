/**
 * Archivo: ActualizarPromocionRequest.kt
 *
 * Define el modelo de datos utilizado para enviar una solicitud
 * de actualización de una promoción existente al servidor.
 *
 * Este modelo se serializa a formato JSON cuando se realiza
 * una petición PUT o PATCH desde el cliente (por ejemplo, usando Retrofit).
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud para actualizar una promoción.
 *
 * Contiene todos los campos necesarios que deben enviarse al endpoint correspondiente
 * para modificar la información de una promoción en el servidor.
 *
 * @property id_promocion Identificador único de la promoción a actualizar
 * @property titulo Título visible de la promoción
 * @property descripcion Descripción detallada de la promoción
 * @property descuento Texto o valor del descuento (por ejemplo, "20%")
 * @property imagen URL o cadena codificada en Base64 de la imagen de la promoción
 */
data class ActualizarPromocionRequest(
    val id_promocion: Int,
    val titulo: String,
    val descripcion: String,
    val descuento: String,
    val imagen: String
)
