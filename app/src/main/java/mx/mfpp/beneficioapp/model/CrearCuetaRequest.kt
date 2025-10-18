package mx.mfpp.beneficioapp.model

/**
 * Representa la solicitud para crear una nueva cuenta de usuario en el sistema **BeneficioApp**.
 *
 * Esta clase agrupa todos los datos requeridos para registrar un nuevo usuario,
 * incluyendo información personal, de contacto, credenciales y consentimiento.
 *
 * @property nombre Nombre del usuario.
 * @property apellidoPaterno Apellido paterno del usuario.
 * @property apellidoMaterno Apellido materno del usuario.
 * @property fechaNacimiento Fecha de nacimiento del usuario en formato "YYYY-MM-DD".
 * @property genero Género del usuario (por ejemplo: "Masculino", "Femenino", "Otro").
 * @property curp CURP del usuario, usada como identificador único oficial.
 * @property correo Dirección de correo electrónico del usuario.
 * @property celular Número de teléfono móvil del usuario.
 * @property password Contraseña elegida para acceder a la cuenta.
 * @property folio_antiguo (Opcional) Folio de una tarjeta física anterior, si aplica.
 * @property tieneTarjeta (Opcional) Indica si el usuario ya cuenta con una tarjeta física activa.
 * @property direccion Objeto [Direccion] que contiene los datos de domicilio del usuario.
 * @property consentimientoAceptado Indica si el usuario aceptó los términos y condiciones del sistema.
 */
data class CrearCuentaRequest(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val fechaNacimiento: String = "",
    val genero: String = "",
    val curp: String = "",
    val correo: String = "",
    val celular: String = "",
    val password: String = "",
    val folio_antiguo: String? = null,
    val tieneTarjeta: Boolean? = null,
    val direccion: Direccion = Direccion(),
    val consentimientoAceptado: Boolean = false
)
