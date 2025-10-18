package mx.mfpp.beneficioapp.model

/**
 * Representa la solicitud de inicio de sesión para un usuario tipo **joven** dentro de **BeneficioApp**.
 *
 * Esta clase se utiliza para enviar las credenciales del usuario al servidor
 * durante el proceso de autenticación, validando su correo y contraseña registrados.
 *
 * @property correo Dirección de correo electrónico asociada a la cuenta del usuario.
 * @property password Contraseña registrada del usuario.
 */
data class LoginRequest(
    val correo: String = "",
    val password: String = ""
)
