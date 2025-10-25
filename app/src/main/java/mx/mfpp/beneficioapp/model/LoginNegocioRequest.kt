package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para
 * iniciar sesión como un negocio (establecimiento).
 *
 * @property correo El correo electrónico registrado por el negocio.
 * @property password La contraseña de la cuenta del negocio.
 */
data class LoginNegocioRequest(
    val correo: String = "",
    val password: String = ""
)