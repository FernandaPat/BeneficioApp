/**
 * Archivo: CrearCuentaResponse.kt
 *
 * Define el modelo de datos para la respuesta recibida al crear
 * una nueva cuenta de usuario en el servidor.
 *
 * Este modelo se utiliza en la capa de red para mapear la respuesta JSON
 * a un objeto Kotlin tras realizar la solicitud de registro.
 */
package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName
/**
 * Representa la estructura de la respuesta del servidor al crear una cuenta.
 *
 * Contiene la información del nuevo usuario registrado, así como
 * el estado y el mensaje de la operación.
 *
 * @property id_usuario Identificador único del usuario recién creado
 * @property nombre Nombre completo del usuario registrado
 * @property folio_digital Folio digital asignado al usuario
 * @property mensaje Mensaje descriptivo devuelto por el servidor
 * @property status Estado de la operación (por ejemplo, "success" o "error")
 */
data class CrearCuentaResponse(

 val id_usuario: String?,
    val nombre: String?,
    val folio_digital: String?,
    val mensaje: String?,
    val status: String?
)
