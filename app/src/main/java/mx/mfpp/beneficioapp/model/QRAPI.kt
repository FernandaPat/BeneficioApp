package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz de Retrofit que define los endpoints para la generación,
 * validación y aplicación de códigos QR relacionados con promociones.
 */
interface QRAPI {

    /**
     * Solicita al servidor la generación de un token QR para un usuario y una promoción.
     *
     * @param request El cuerpo [GenerarQRRequest] que contiene el ID de usuario y el ID de promoción.
     * @return Una [Response] de Retrofit que envuelve un [QRTokenResponse] con el token generado.
     */
    @POST("generar-qr")
    suspend fun generarQR(@Body request: GenerarQRRequest): Response<QRTokenResponse>

    /**
     * Envía un token QR al servidor para su validación (usualmente por parte del negocio).
     *
     * @param request El cuerpo [ValidarQRRequest] que contiene el token QR a validar.
     * @return Una [Response] de Retrofit que envuelve un [QRValidationResponse] con los detalles de la validación.
     */
    @POST("validar-qr")
    suspend fun validarQR(@Body request: ValidarQRRequest): Response<QRValidationResponse>

    /**
     * Confirma y aplica (canjea) una promoción después de que el QR ha sido validado.
     *
     * @param request El cuerpo [AplicarPromocionRequest] que contiene los IDs necesarios para el canje.
     * @return Una [Response] de Retrofit que envuelve un [AplicarPromocionResponse] confirmando el resultado.
     */
    @POST("aplicar-promocion")
    suspend fun aplicarPromocion(@Body request: AplicarPromocionRequest): Response<AplicarPromocionResponse>
}