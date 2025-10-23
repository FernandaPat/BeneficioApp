/**
 * Archivo: CrearCuentaRequest.kt
 *
 * Define el modelo de datos utilizado para enviar una solicitud
 * de creación de cuenta de usuario joven al servidor.
 *
 * Este modelo se serializa a formato JSON al realizar una
 * petición POST desde el cliente (por ejemplo, mediante Retrofit).
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura del cuerpo de una solicitud para crear una nueva cuenta de usuario.
 *
 * Contiene los datos personales, de contacto y de autenticación necesarios para el registro.
 *
 * @property nombre Nombre del usuario
 * @property apellidoPaterno Primer apellido del usuario
 * @property apellidoMaterno Segundo apellido del usuario
 * @property curp CURP del usuario
 * @property genero Género del usuario (por ejemplo, "Masculino" o "Femenino")
 * @property correo Correo electrónico del usuario
 * @property celular Número de teléfono móvil del usuario
 * @property password Contraseña de acceso
 * @property fechaNacimiento Fecha de nacimiento en formato dd/MM/yyyy
 * @property direccion Objeto que contiene la información de dirección del usuario
 * @property folio_antiguo Folio físico anterior (si el usuario tenía tarjeta previa)
 * @property tieneTarjeta Indica si el usuario ya contaba con una tarjeta física
 * @property consentimientoAceptado Indica si el usuario aceptó los términos y condiciones
 */
data class CrearCuentaRequest(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val curp: String = "",
    val genero: String = "",
    val correo: String = "",
    val celular: String = "",
    val password: String = "",
    val fechaNacimiento: String = "",
    val direccion: Direccion = Direccion(),
    val folio_antiguo: String? = null,
    val tieneTarjeta: Boolean? = null,
    val consentimientoAceptado: Boolean = false
)
