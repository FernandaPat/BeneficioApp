package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.Response

/**
 * Interfaz de Retrofit para definir el endpoint que elimina una promoción.
 * (Posiblemente una Cloud Function).
 */
interface ApiEliminarPromocion {

    /**
     * Envía una solicitud DELETE para eliminar una promoción específica usando su ID.
     *
     * @param idPromocion El identificador único de la promoción que se desea eliminar.
     * @return Una [Response] de Retrofit que envuelve [Unit], indicando
     * que no se espera un cuerpo de respuesta específico (ej. HTTP 204 No Content).
     */
    @DELETE("eliminarPromocion/{id_promocion}")
    suspend fun eliminarPromocion(@Path("id_promocion") idPromocion: Int): Response<Unit>
}

/**
 * Objeto (Singleton) que proporciona una instancia de Retrofit
 * configurada para el servicio remoto de eliminar promociones.
 */
object ServicioRemotoEliminarPromocion {

    /** URL base del servicio (Cloud Function) para eliminar promociones. */
    private const val BASE_URL = "https://eliminar-promocion-819994103285.us-central1.run.app/"

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
     * Instancia de la [ApiEliminarPromocion] creada por Retrofit.
     * Se inicializa de forma perezosa (lazy).
     */
    val api: ApiEliminarPromocion by lazy {
        retrofit.create(ApiEliminarPromocion::class.java)
    }
}