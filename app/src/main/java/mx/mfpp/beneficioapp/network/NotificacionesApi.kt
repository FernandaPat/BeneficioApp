package mx.mfpp.beneficioapp.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz de Retrofit que define los endpoints para el registro
 * de tokens de notificación (ej. FCM).
 */
interface NotificacionesApi {
    /**
     * Envía el token del dispositivo de un usuario al servidor para
     * registrarlo y poder enviar notificaciones push.
     *
     * @param request El objeto [RegistrarTokenRequest] que contiene el ID
     * de usuario y el token del dispositivo.
     * @return Una [Response] de Retrofit que envuelve [RegistrarTokenResponse]
     * confirmando el registro.
     */
    @POST("/")
    suspend fun registrarToken(
        @Body request: RegistrarTokenRequest
    ): Response<RegistrarTokenResponse>
}

/**
 * Modelo de datos que representa la solicitud (request) para registrar
 * un token de dispositivo (FCM) en el servidor.
 *
 * @property id_usuario El identificador único del usuario al que pertenece el token.
 * @property device_token El token de registro del dispositivo (ej. FCM Token).
 * @property plataforma El sistema operativo del dispositivo (default: "android").
 */
data class RegistrarTokenRequest(
    val id_usuario: Int,
    val device_token: String,
    val plataforma: String = "android"
)

/**
 * Modelo de datos que representa la respuesta (response) del servidor
 * tras un intento de registro de token.
 *
 * @property message Un mensaje descriptivo del resultado (ej. "Token registrado").
 * @property id_usuario El ID del usuario asociado al registro.
 * @property plataforma La plataforma registrada.
 */
data class RegistrarTokenResponse(
    val message: String,
    val id_usuario: Int,
    val plataforma: String
)