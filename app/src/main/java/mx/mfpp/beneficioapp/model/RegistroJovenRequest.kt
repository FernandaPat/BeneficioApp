/**
 * Archivo: RegistroJovenRequest.kt
 *
 * Define el modelo de datos utilizado para enviar una solicitud
 * de registro de un nuevo usuario joven al servidor.
 *
 * Este modelo se serializa en formato JSON al realizar una petición POST
 * mediante Retrofit.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud para registrar un nuevo usuario joven.
 *
 * Contiene los datos personales, de contacto y de autenticación necesarios
 * para crear una cuenta en el sistema.
 *
 * @property nombre Nombre del usuario joven
 * @property apellido_paterno Primer apellido del usuario
 * @property apellido_materno Segundo apellido del usuario
 * @property curp CURP del usuario (Clave Única de Registro de Población)
 * @property fecha_nacimiento Fecha de nacimiento del usuario en formato dd/MM/yyyy
 * @property genero Género del usuario (por ejemplo, "Masculino", "Femenino" u "Otro")
 * @property correo Correo electrónico del usuario
 * @property telefono Número de teléfono del usuario
 * @property contrasena Contraseña de acceso para la cuenta
 * @property direccion Objeto [DireccionRequest] con los datos completos de la dirección del usuario
 */
data class RegistroJovenRequest(
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val curp: String,
    val fecha_nacimiento: String,
    val genero: String,
    val correo: String,
    val telefono: String,
    val contrasena: String,
    val direccion: DireccionRequest
)