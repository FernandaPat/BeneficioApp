package mx.mfpp.beneficioapp.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto (Singleton) que gestiona la comunicación con las APIs remotas
 * relacionadas con el ciclo de vida de los Códigos QR (generar, validar y aplicar).
 *
 * Utiliza instancias de Retrofit separadas para cada endpoint,
 * ya que apuntan a URLs base (Cloud Functions) distintas.
 */
object ServicioRemotoQR {

    /** URL base para el servicio (Cloud Function) de *generación* de QR. */
    private const val URL_BASE_GENERAR = "https://generar-qr-819994103285.us-central1.run.app/"
    /** URL base para el servicio (Cloud Function) de *validación* de QR. */
    private const val URL_BASE_VALIDAR = "https://validar-qr-819994103285.us-central1.run.app/"
    /** URL base para el servicio (Cloud Function) de *aplicación* (canje) de promoción. */
    private const val URL_BASE_APLICAR = "https://aplicar-promocion-819994103285.us-central1.run.app/"

    /** Instancia [Retrofit] configurada para [URL_BASE_GENERAR]. Se inicializa (lazy). */
    private val retrofitGenerar: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE_GENERAR)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Instancia [Retrofit] configurada para [URL_BASE_VALIDAR]. Se inicializa (lazy). */
    private val retrofitValidar: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE_VALIDAR)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Instancia [Retrofit] configurada para [URL_BASE_APLICAR]. Se inicializa (lazy). */
    private val retrofitAplicar: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE_APLICAR)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Instancia de [QRAPI] (servicio) creada desde [retrofitGenerar]. */
    private val apiGenerar: QRAPI by lazy {
        retrofitGenerar.create(QRAPI::class.java)
    }

    /** Instancia de [QRAPI] (servicio) creada desde [retrofitValidar]. */
    private val apiValidar: QRAPI by lazy {
        retrofitValidar.create(QRAPI::class.java)
    }

    /** Instancia de [QRAPI] (servicio) creada desde [retrofitAplicar]. */
    private val apiAplicar: QRAPI by lazy {
        retrofitAplicar.create(QRAPI::class.java)
    }

    /**
     * Solicita la generación de un token QR al servidor.
     *
     * Maneja códigos de error HTTP (4xx, 5xx) para proveer mensajes de
     * error específicos legibles por el usuario.
     *
     * @param idJoven El ID del usuario (joven) que solicita el QR.
     * @param idPromocion El ID de la promoción para la que se genera el QR.
     * @return Un [Pair] que contiene:
     * - `first`: El [QRTokenResponse] si la solicitud es exitosa (HTTP 2xx).
     * - `second`: Un mensaje de error [String] legible para el usuario si
     * la solicitud falla (HTTP no 2xx o excepción de red).
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
     * Envía un token QR (obtenido del scanner) al servidor para su validación.
     *
     * @param token El token (String) leído desde el código QR.
     * @param idEstablecimiento El ID del establecimiento que realiza la validación.
     * @return El [QRValidationResponse] si la validación es exitosa (HTTP 2xx).
     * Retorna `null` si la respuesta HTTP no es 2xx o si ocurre una excepción de red.
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
     * Confirma y aplica (canjea) una promoción después de una validación de QR exitosa.
     *
     * @param idTarjeta El ID de la tarjeta del usuario que canjea.
     * @param idPromocion El ID de la promoción que se está canjeando.
     * @param idEstablecimiento El ID del establecimiento donde se realiza el canje.
     * @return El [AplicarPromocionResponse] si el canje es exitoso (HTTP 2xx).
     * Retorna `null` si la respuesta HTTP no es 2xx o si ocurre una excepción de red.
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