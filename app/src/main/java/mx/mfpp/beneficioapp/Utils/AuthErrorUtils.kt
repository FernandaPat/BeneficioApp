package mx.mfpp.beneficioapp.utils

import com.auth0.android.authentication.AuthenticationException

/**
 * Objeto (Singleton) que proporciona funciones de utilidad para
 * manejar y traducir errores de autenticación, específicamente [AuthenticationException]
 * de Auth0.
 */
object AuthErrorUtils {

    /**
     * Convierte una [AuthenticationException] de Auth0 en un mensaje [String]
     * legible para el usuario final (en español).
     *
     * Analiza el código de error (`code`) y la descripción (`desc`) de la excepción
     * para identificar la causa raíz (ej. sin conexión, credenciales inválidas, etc.).
     *
     * @param error La [AuthenticationException] original recibida del SDK de Auth0.
     * @return Un [String] con el mensaje de error localizado y amigable.
     */
    fun obtenerMensaje(error: AuthenticationException): String {
        val desc = error.getDescription() ?: ""
        val code = error.getCode() ?: ""

        return when {
            desc.contains("Unable to resolve host", true) ||
                    desc.contains("No address associated", true) ->
                "Sin conexión a Internet. Por favor verifica tu red e inténtalo de nuevo."

            desc.contains("a0.sdk.internal_error.unknown", true) ->
                "Sin conexión a Internet. Por favor verifica tu red e inténtalo de nuevo."

            code == "invalid_grant" || code == "invalid_user_password" ->
                "Correo o contraseña incorrectos. Intenta de nuevo."

            code == "too_many_attempts" ->
                "Has realizado demasiados intentos. Espera un momento antes de volver a intentar."

            code == "unauthorized" ->
                "Acceso no autorizado. Verifica tus datos."

            else -> "Ocurrió un error al iniciar sesión. Inténtalo nuevamente."
        }
    }
}
