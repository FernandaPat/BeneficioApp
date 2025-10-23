/**
 * Archivo: EditarPromocionResponse.kt
 *
 * Define el modelo de datos para la respuesta recibida al editar
 * una promoción existente en el servidor.
 *
 * Este modelo se utiliza en la capa de red para mapear la respuesta JSON
 * a un objeto Kotlin después de una solicitud PUT o PATCH.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura de la respuesta del servidor tras editar una promoción.
 *
 * Incluye el estado de la operación y un mensaje descriptivo.
 *
 * @property mensaje Mensaje informativo devuelto por el servidor (por ejemplo, "Promoción actualizada correctamente")
 * @property status Estado de la operación (por ejemplo, "success" o "error")
 */
data class EditarPromocionResponse(
    val mensaje: String?,
    val status: String?
)
