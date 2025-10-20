package mx.mfpp.beneficioapp.utils

import com.auth0.android.authentication.AuthenticationException

object AuthErrorUtils {

    fun obtenerMensaje(error: AuthenticationException): String {
        val desc = error.getDescription() ?: ""
        val code = error.getCode() ?: ""

        return when {
            // Sin conexión
            desc.contains("Unable to resolve host", true) ||
                    desc.contains("No address associated", true) ->
                "Sin conexión a Internet. Por favor verifica tu red e inténtalo de nuevo."

            // Error interno de Auth0
            desc.contains("a0.sdk.internal_error.unknown", true) ->
                "Sin conexión a Internet. Por favor verifica tu red e inténtalo de nuevo."

            // Credenciales incorrectas
            code == "invalid_grant" || code == "invalid_user_password" ->
                "Correo o contraseña incorrectos. Intenta de nuevo."

            // Demasiados intentos
            code == "too_many_attempts" ->
                "Has realizado demasiados intentos. Espera un momento antes de volver a intentar."

            // Acceso no autorizado
            code == "unauthorized" ->
                "Acceso no autorizado. Verifica tus datos."

            // Error genérico
            else -> "Ocurrió un error al iniciar sesión. Inténtalo nuevamente."
        }
    }
}
