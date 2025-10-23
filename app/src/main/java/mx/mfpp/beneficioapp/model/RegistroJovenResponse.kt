/**
 * Archivo: RegistroJovenResponse.kt
 *
 * Define el modelo de datos que representa la respuesta del servidor
 * tras realizar una solicitud de registro de un nuevo usuario joven.
 *
 * Este modelo se utiliza para mapear la respuesta JSON enviada por la API
 * después de crear la cuenta.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la respuesta del servidor al registrar un nuevo usuario joven.
 *
 * Contiene información sobre el resultado de la operación y los datos básicos
 * del usuario recién creado.
 *
 * @property id_usuario Identificador único asignado al usuario joven (opcional)
 * @property mensaje Mensaje descriptivo del resultado de la operación (opcional)
 * @property status Estado de la operación (por ejemplo, "success" o "error") (opcional)
 */
data class RegistroJovenResponse(
    val id_usuario: Int?,
    val mensaje: String?,
    val status: String?
)
