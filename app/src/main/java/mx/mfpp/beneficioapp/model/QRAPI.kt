package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QRAPI {
    @POST("generar-qr")
    suspend fun generarQR(@Body request: GenerarQRRequest): Response<QRTokenResponse>

    @POST("validar-qr")
    suspend fun validarQR(@Body request: ValidarQRRequest): Response<QRValidationResponse>

    @POST("aplicar-promocion")
    suspend fun aplicarPromocion(@Body request: AplicarPromocionRequest): Response<AplicarPromocionResponse>
}
