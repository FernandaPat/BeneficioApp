/**
 * Archivo: LoginNegocioRequest.kt
 *
 * Define el modelo de datos utilizado para enviar una solicitud de inicio de sesión
 * de un negocio al servidor.
 *
 * Este modelo se serializa a formato JSON al realizar una petición POST
 * desde el cliente (por ejemplo, mediante Retrofit).
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud de inicio de sesión para negocios.
 *
 * @property correo Correo electrónico del negocio registrado
 * @property password Contraseña asociada a la cuenta del negocio
 */
data class LoginNegocioRequest(
    val correo: String = "",
    val password: String = ""
)
