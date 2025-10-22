package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QRAPI {
    @POST("generar-qr")
    suspend fun generarQR(@Body request: GenerarQRRequest): Response<QRTokenResponse>
}
