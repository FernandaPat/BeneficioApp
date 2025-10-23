/**
 * Archivo: QRAPI.kt
 *
 * Define la interfaz de comunicación con los endpoints relacionados con códigos QR
 * mediante Retrofit.
 *
 * Proporciona los métodos para generar, validar y aplicar promociones
 * a través de códigos QR.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
/**
 * Interfaz que define las operaciones HTTP relacionadas con el manejo de códigos QR.
 *
 * Incluye la generación, validación y aplicación de promociones utilizando solicitudes POST.
 */
interface QRAPI {
    /**
     * Genera un nuevo código QR asociado a una promoción o usuario.
     *
     * @param request Cuerpo de la solicitud que contiene los datos necesarios para generar el QR
     * @return Respuesta HTTP con un objeto [QRTokenResponse] que contiene el token o datos del código QR generado
     */
    @POST("generar-qr")
    suspend fun generarQR(@Body request: GenerarQRRequest): Response<QRTokenResponse>
    /**
     * Valida un código QR escaneado para verificar su autenticidad o vigencia.
     *
     * @param request Cuerpo de la solicitud con los datos del código QR a validar
     * @return Respuesta HTTP con un objeto [QRValidationResponse] que indica si el QR es válido
     */
    @POST("validar-qr")
    suspend fun validarQR(@Body request: ValidarQRRequest): Response<QRValidationResponse>
    /**
     * Aplica una promoción asociada a un código QR previamente validado.
     *
     * @param request Cuerpo de la solicitud con los datos necesarios para aplicar la promoción
     * @return Respuesta HTTP con un objeto [AplicarPromocionResponse] que indica el resultado de la operación
     */
    @POST("aplicar-promocion")
    suspend fun aplicarPromocion(@Body request: AplicarPromocionRequest): Response<AplicarPromocionResponse>
}
