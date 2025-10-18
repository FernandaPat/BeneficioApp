package mx.mfpp.beneficioapp.model

/**
 * Representa la solicitud de inicio de sesión para un negocio dentro de **BeneficioApp**.
 *
 * Esta clase se utiliza para enviar las credenciales del establecimiento al servidor
 * con el fin de autenticar al usuario del tipo "negocio" y permitirle acceder a su cuenta.
 *
 * @property correo Dirección de correo electrónico registrada del negocio.
 * @property password Contraseña asociada a la cuenta del negocio.
 */
data class LoginNegocioRequest(
    val correo: String = "",
    val password: String = ""
)
