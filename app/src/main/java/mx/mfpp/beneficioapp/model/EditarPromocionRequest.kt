/**
 * Archivo: EditarPromocionRequest.kt
 *
 * Define el modelo de datos utilizado para enviar una solicitud
 * de edición o actualización parcial de una promoción existente.
 *
 * Este modelo se serializa en formato JSON al realizar una
 * petición PUT o PATCH desde el cliente hacia el servidor.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud para editar una promoción.
 *
 * Incluye los campos que pueden ser modificados por el negocio
 * al actualizar una promoción existente.
 *
 * @property id_promocion Identificador único de la promoción a editar
 * @property titulo_promocion Título actualizado de la promoción
 * @property descripcion Descripción modificada de la promoción
 * @property descuento Nuevo valor o porcentaje de descuento (opcional)
 * @property categoria Categoría de la promoción
 * @property disponible_desde Nueva fecha de inicio de validez (opcional, formato dd/MM/yyyy)
 * @property hasta Nueva fecha de finalización (opcional, formato dd/MM/yyyy)
 * @property imagen URL o cadena codificada en Base64 de la nueva imagen (opcional)
 */
data class EditarPromocionRequest(
    val id_promocion: Int,
    val titulo_promocion: String,
    val descripcion: String,
    val descuento: String?,
    val categoria: String,
    val disponible_desde: String?,
    val hasta: String?,
    val imagen: String?
)
