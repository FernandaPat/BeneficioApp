/**
 * Archivo: ServicioRemotoQR.kt
 *
 * Define el servicio remoto responsable de generar, validar y aplicar códigos QR
 * relacionados con promociones dentro de la aplicación.
 *
 * Utiliza Retrofit con Gson para realizar solicitudes HTTP hacia los servicios
 * desplegados en Google Cloud Run.
 */
package mx.mfpp.beneficioapp.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Objeto singleton encargado de la comunicación con los tres servicios remotos
 * vinculados al flujo de uso de códigos QR:
 *
 * - **Generar QR**: crea un token único para canjear una promoción.
 * - **Validar QR**: verifica la validez del token escaneado.
 * - **Aplicar promoción**: registra el canje de una promoción validada.
 *
 * Cada operación utiliza su propia instancia de Retrofit configurada con su URL base.
 */
object ServicioRemotoQR {

    private const val URL_BASE_GENERAR = "https://generar-qr-819994103285.us-central1.run.app/"
    private const val URL_BASE_VALIDAR = "https://validar-qr-819994103285.us-central1.run.app/"
    private const val URL_BASE_APLICAR = "https://aplicar-promocion-819994103285.us-central1.run.app/"

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

    // Retrofit Aplicar
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

    // API Aplicar
    private val apiAplicar: QRAPI by lazy {
        retrofitAplicar.create(QRAPI::class.java)
    }
    /**
     * Genera un código QR para un usuario joven y una promoción específica.
     *
     * @param idJoven Identificador del usuario joven.
     * @param idPromocion Identificador de la promoción a canjear.
     * @return Par compuesto por el objeto [QRTokenResponse] si la solicitud es exitosa,
     * y un mensaje de error en caso contrario.
     */
    suspend fun generarQR(idJoven: Int, idPromocion: Int): Pair<QRTokenResponse?, String?> {
        return try {
            val response = apiGenerar.generarQR(GenerarQRRequest(idJoven, idPromocion))
            if (response.isSuccessful) {
                Pair(response.body(), null)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    400 -> "Parece que hubo un problema con la solicitud. Por favor intenta de nuevo."
                    401 -> "Tu sesión expiró. Vuelve a iniciar sesión para continuar."
                    403 -> "No tienes permiso para realizar esta acción."
                    404 -> "No pudimos encontrar la promoción. Es posible que ya no esté disponible."
                    500 -> "Estamos teniendo un problema en el servidor. Inténtalo más tarde."
                    503 -> "El servicio no está disponible por el momento. Vuelve a intentarlo pronto."
                    else -> "Ocurrió un error inesperado. Código ${response.code()}."
                }
                Pair(null, errorMessage)
            }
        } catch (e: Exception) {
            Pair(null, "Error de conexión: ${e.localizedMessage ?: "No se pudo conectar al servidor."}")
        }
    }
    /**
     * Valida un token QR escaneado en un establecimiento.
     *
     * @param token Código QR generado previamente.
     * @param idEstablecimiento Identificador del establecimiento que realiza la validación.
     * @return Objeto [QRValidationResponse] con el resultado de la validación, o `null` si falla.
     */

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

    /**
     * Aplica una promoción validada para un usuario joven en un establecimiento.
     *
     * Envía al servidor la información de la tarjeta, la promoción y el establecimiento,
     * registrando el canje de la promoción.
     *
     * @param idTarjeta Identificador de la tarjeta digital del usuario.
     * @param idPromocion Identificador de la promoción aplicada.
     * @param idEstablecimiento Identificador del establecimiento donde se aplica la promoción.
     * @return Objeto [AplicarPromocionResponse] con los detalles del registro, o `null` si ocurre un error.
     */
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