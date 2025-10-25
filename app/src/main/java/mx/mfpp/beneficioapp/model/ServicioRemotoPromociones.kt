package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de Retrofit que define los endpoints para
 * obtener la lista de promociones (posiblemente de una Cloud Function).
 */
interface ApiPromociones {
    /**
     * Obtiene la lista de promociones filtradas por un ID de establecimiento (negocio).
     *
     * @param establecimientoId El identificador único del establecimiento
     * para filtrar los resultados.
     * @return Una [Response] de Retrofit que envuelve [PromocionesResponse].
     */
    @GET("/")
    suspend fun getPromocionesPorNegocio(
        @Query("establecimiento_id") establecimientoId: Int
    ): Response<PromocionesResponse>
}

/**
 * Objeto (Singleton) que proporciona una instancia de Retrofit
 * configurada para el servicio remoto de listar promociones.
 *
 * Expone la instancia de [ApiPromociones] lista para ser usada.
 */
object ServicioRemotoPromociones {
    /** URL base del servicio (Cloud Function) para listar promociones. */
    private const val BASE_URL = "https://listar-promociones-819994103285.us-central1.run.app/"

    /**
     * Instancia pública de [ApiPromociones], creada por Retrofit y
     * configurada con [BASE_URL].
     * Se inicializa de forma perezosa (lazy).
     */
    val api: ApiPromociones by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiPromociones::class.java)
    }
}