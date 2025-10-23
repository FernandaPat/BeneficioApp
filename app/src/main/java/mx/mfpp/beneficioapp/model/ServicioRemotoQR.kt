package mx.mfpp.beneficioapp.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicioRemotoQR {

    private const val URL_BASE_GENERAR = "https://generar-qr-819994103285.us-central1.run.app/"
    private const val URL_BASE_VALIDAR = "https://validar-qr-819994103285.us-central1.run.app/"
    private const val URL_BASE_APLICAR = "https://aplicar-promocion-819994103285.us-central1.run.app/" // ✅ AGREGAR

    // Retrofit Generar
    private val retrofitGenerar: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE_GENERAR)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit Validar
    private val retrofitValidar: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE_VALIDAR)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ✅ AGREGAR: Retrofit Aplicar
    private val retrofitAplicar: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE_APLICAR)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiGenerar: QRAPI by lazy {
        retrofitGenerar.create(QRAPI::class.java)
    }

    private val apiValidar: QRAPI by lazy {
        retrofitValidar.create(QRAPI::class.java)
    }

    // ✅ AGREGAR: API Aplicar
    private val apiAplicar: QRAPI by lazy {
        retrofitAplicar.create(QRAPI::class.java)
    }

    suspend fun generarQR(idJoven: Int, idPromocion: Int): QRTokenResponse? {
        return try {
            Log.d("SERVICIO_QR", "Generando QR para joven $idJoven y promoción $idPromocion")
            val response = apiGenerar.generarQR(GenerarQRRequest(idJoven, idPromocion))
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

    suspend fun validarQR(token: String, idEstablecimiento: Int): QRValidationResponse? {
        return try {
            Log.d("SERVICIO_QR", "Validando QR en establecimiento $idEstablecimiento")
            val response = apiValidar.validarQR(ValidarQRRequest(token, idEstablecimiento))
            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("SERVICIO_QR", "Error validarQR: ${response.code()} - $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("SERVICIO_QR", "Excepción validarQR: ${e.message}", e)
            null
        }
    }

    // ✅ AGREGAR: Función para aplicar promoción
    suspend fun aplicarPromocion(
        idTarjeta: Int,
        idPromocion: Int,
        idEstablecimiento: Int
    ): AplicarPromocionResponse? {
        return try {
            Log.d("SERVICIO_QR", "Aplicando promoción: tarjeta=$idTarjeta, promocion=$idPromocion, establecimiento=$idEstablecimiento")
            val response = apiAplicar.aplicarPromocion(
                AplicarPromocionRequest(idTarjeta, idPromocion, idEstablecimiento)
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("SERVICIO_QR", "Error aplicarPromocion: ${response.code()} - $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("SERVICIO_QR", "Excepción aplicarPromocion: ${e.message}", e)
            null
        }
    }
}