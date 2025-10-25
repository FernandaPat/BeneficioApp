package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

/**
 * Interfaz de Retrofit para definir el endpoint de creación de cuentas de usuario (joven).
 * (Posiblemente una Cloud Function o Cloud Run).
 */
interface ApiCrearCuenta {

    /**
     * Envía los datos de un nuevo usuario para registrarlo en el sistema.
     *
     * @param request El objeto [CrearCuentaRequest] que contiene toda la
     * información del usuario a registrar.
     * @return Una [Response] de Retrofit que envuelve [Any] (tipo genérico),
     * ya que la respuesta específica (éxito o error) será manejada
     * por el interceptor o en la capa superior.
     */
    @POST("registroJoven")
    suspend fun registrarUsuario(@Body request: CrearCuentaRequest): Response<Any>
}

/**
 * Objeto (Singleton) que proporciona una instancia de Retrofit
 * configurada para el servicio remoto de creación de cuentas.
 */
object ServicioRemotoCrearCuenta {

    /** URL base del servicio (Cloud Function/Run) para registrar jóvenes. */
    private const val BASE_URL = "https://registrojoven-819994103285.us-central1.run.app/"

    /**
     * Instancia [Retrofit] configurada con la [BASE_URL] y [GsonConverterFactory].
     * Se inicializa de forma perezosa (lazy).
     */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Instancia de la [ApiCrearCuenta] creada por Retrofit.
     * Se inicializa de forma perezosa (lazy).
     */
    val api: ApiCrearCuenta by lazy {
        retrofit.create(ApiCrearCuenta::class.java)
    }
}