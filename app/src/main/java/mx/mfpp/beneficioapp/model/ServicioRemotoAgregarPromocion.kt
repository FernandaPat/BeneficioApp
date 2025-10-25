package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Interfaz de Retrofit para definir el endpoint que agrega una nueva promoción.
 * (Posiblemente una Cloud Function).
 */
interface ApiAgregarPromocion {
    /**
     * Envía una nueva promoción al servidor.
     *
     * @param request El objeto [AgregarPromocionRequest] que contiene
     * los detalles de la promoción a crear.
     * @return Una [Response] de Retrofit que envuelve [Unit], indicando
     * que no se espera un cuerpo de respuesta específico (ej. HTTP 201 Created).
     */
    @Headers("Content-Type: application/json")
    @POST("/") // El endpoint es la raíz de la BASE_URL
    suspend fun agregarPromocion(@Body request: AgregarPromocionRequest): Response<Unit>
}

/**
 * Objeto (Singleton) que proporciona una instancia de Retrofit
 * configurada para el servicio remoto de agregar promociones.
 */
object ServicioRemotoAgregarPromocion {
    /** URL base del servicio (Cloud Function) para agregar promociones. */
    private const val BASE_URL = "https://agregar-promocion-819994103285.us-central1.run.app/"

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
     * Instancia de la [ApiAgregarPromocion] creada por Retrofit.
     * Se inicializa de forma perezosa (lazy).
     */
    val api: ApiAgregarPromocion by lazy {
        retrofit.create(ApiAgregarPromocion::class.java)
    }
}