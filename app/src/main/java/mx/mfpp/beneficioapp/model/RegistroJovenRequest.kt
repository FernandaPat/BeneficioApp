package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para registrar
 * un nuevo usuario (joven).
 *
 * @property nombre El nombre de pila del usuario.
 * @property apellido_paterno El apellido paterno del usuario.
 * @property apellido_materno El apellido materno del usuario.
 * @property curp La Clave Única de Registro de Población (CURP) del usuario.
 * @property fecha_nacimiento La fecha de nacimiento (en formato String).
 * @property genero El género del usuario (ej. "Masculino", "Femenino").
 * @property correo La dirección de correo electrónico del usuario.
 *Anteriormente:
package mx.mfpp.beneficioapp.model

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
) * @property telefono El número de teléfono celular del usuario.
 * @property contrasena La contraseña elegida por el usuario para su cuenta.
 * @property direccion El objeto [DireccionRequest] que contiene los detalles
 * de la dirección del usuario.
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