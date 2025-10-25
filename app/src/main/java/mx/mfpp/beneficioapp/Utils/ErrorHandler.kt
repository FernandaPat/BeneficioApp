package mx.mfpp.beneficioapp.utils

import retrofit2.HttpException
import java.io.IOException
import com.auth0.android.authentication.AuthenticationException

/**
 * Objeto (Singleton) que centraliza el manejo de excepciones
 * y las traduce a mensajes legibles para el usuario (en español).
 *
 * Especializado en manejar errores comunes de red ([IOException]),
 * errores HTTP de Retrofit ([HttpException]), y errores de
 * autenticación de Auth0 ([AuthenticationException]).
 */
object ErrorHandler {

    /**
     * Procesa una [Exception] genérica y devuelve un mensaje [String]
     * amigable para el usuario.
     *
     * @param e La excepción capturada (ej. [IOException], [HttpException],
     * [AuthenticationException], u otra).
     * @return Un [String] que describe el error de forma sencilla.
     */
    fun obtenerMensajeError(e: Exception): String {
        return when (e) {
            /** Error de red, como falta de conectividad. */
            is IOException -> {
                "Sin conexión a Internet. Por favor verifica tu red e inténtalo nuevamente."
            }
            /** Error HTTP (respuesta 4xx o 5xx) de Retrofit. */
            is HttpException -> {
                when (e.code()) {
                    400 -> "Solicitud incorrecta. Verifica los datos ingresados."
                    401 -> "No autorizado. Vuelve a iniciar sesión."
                    404 -> "No se encontró la información solicitada."
                    500 -> "Error del servidor. Inténtalo nuevamente más tarde."
                    else -> "Error desconocido del servidor (${e.code()})."
                }
            }
            /** Error específico de la librería Auth0. */
            is AuthenticationException -> {
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
            /** Cualquier otra excepción no manejada específicamente. */
            else -> {
                "Ocurrió un error inesperado. Intenta nuevamente."
            }
        }
    }
}