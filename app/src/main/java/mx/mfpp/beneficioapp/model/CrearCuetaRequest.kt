package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para crear
 * una nueva cuenta de usuario.
 *
 * @property nombre El nombre de pila del usuario.
 * @property apellidoPaterno El apellido paterno del usuario.
 * @property apellidoMaterno El apellido materno del usuario.
 * @property curp La Clave Única de Registro de Población (CURP) del usuario.
 * @property genero El género del usuario (ej. "Masculino", "Femenino").
 * @property correo La dirección de correo electrónico del usuario.
 * @property celular El número de teléfono celular del usuario.
 * @property password La contraseña elegida por el usuario para su cuenta.
 * @property fechaNacimiento La fecha de nacimiento del usuario (en formato String).
 * @property direccion El objeto [Direccion] que contiene los detalles de la dirección del usuario.
 * @property folio_antiguo El folio de una tarjeta o cuenta antigua (opcional, puede ser nulo).
 * @property tieneTarjeta Indica si el usuario ya posee una tarjeta física (opcional, puede ser nulo).
 * @property consentimientoAceptado Indica si el usuario aceptó los términos, condiciones o aviso de privacidad.
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