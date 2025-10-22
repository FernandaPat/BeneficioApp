import android.util.Log
import mx.mfpp.beneficioapp.model.GenerarQRRequest
import mx.mfpp.beneficioapp.model.QRAPI
import mx.mfpp.beneficioapp.model.QRTokenResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicioRemotoQR {

    private const val URL_BASE = "https://generar-qr-819994103285.us-central1.run.app/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api: QRAPI by lazy {
        retrofit.create(QRAPI::class.java)
    }

    suspend fun generarQR(idJoven: Int, idPromocion: Int): QRTokenResponse? {
        return try {
            Log.d("SERVICIO_QR", "Generando QR para joven $idJoven y promoción $idPromocion")
            val response = api.generarQR(GenerarQRRequest(idJoven, idPromocion))
            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("SERVICIO_QR", "Error: ${response.code()} - $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("SERVICIO_QR", "Excepción: ${e.message}", e)
            null
        }
    }
}