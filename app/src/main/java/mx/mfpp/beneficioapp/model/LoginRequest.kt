package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para
 * iniciar sesión como un usuario (joven).
 *
 * @property correo El correo electrónico registrado por el usuario.
 * @property password La contraseña de la cuenta del usuario.
 */
data class LoginRequest(
    val correo: String = "",
    val password: String = ""
)