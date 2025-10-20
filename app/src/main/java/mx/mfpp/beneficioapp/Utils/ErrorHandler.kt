package mx.mfpp.beneficioapp.utils

import retrofit2.HttpException
import java.io.IOException
import com.auth0.android.authentication.AuthenticationException

object ErrorHandler {

    fun obtenerMensajeError(e: Exception): String {
        return when (e) {
            is IOException -> {
                // Sin conexión a internet o error de red
                "Sin conexión a Internet. Por favor verifica tu red e inténtalo nuevamente."
            }
            is HttpException -> {
                when (e.code()) {
                    400 -> "Solicitud incorrecta. Verifica los datos ingresados."
                    401 -> "No autorizado. Vuelve a iniciar sesión."
                    404 -> "No se encontró la información solicitada."
                    500 -> "Error del servidor. Inténtalo nuevamente más tarde."
                    else -> "Error desconocido del servidor (${e.code()})."
                }
            }
            is AuthenticationException -> {
                // Si también usas Auth0 aquí
                val desc = e.getDescription() ?: ""
                val code = e.getCode() ?: ""
                when {
                    desc.contains("Unable to resolve host", true) ||
                            desc.contains("No address associated", true) ->
                        "Sin conexión a Internet. Verifica tu red e inténtalo nuevamente."
                    desc.contains("a0.sdk.internal_error.unknown", true) ->
                        "Error interno del servidor. Intenta nuevamente más tarde."
                    code == "invalid_user_password" ->
                        "Correo o contraseña incorrectos."
                    else -> "Ocurrió un problema al iniciar sesión. Inténtalo más tarde."
                }
            }
            else -> {
                "Ocurrió un error inesperado. Intenta nuevamente."
            }
        }
    }
}
