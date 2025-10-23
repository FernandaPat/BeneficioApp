/**
 * Archivo: ServicioRemotoAgregarPromocion.kt
 *
 * Define la interfaz y el servicio remoto para agregar una nueva promoción
 * mediante una solicitud HTTP POST al backend.
 *
 * Utiliza Retrofit con conversión JSON mediante Gson.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
/**
 * Interfaz Retrofit que define el endpoint para agregar una promoción.
 *
 * Envía los datos de la promoción en formato JSON mediante una solicitud POST.
 */
interface ApiAgregarPromocion {
    /**
     * Envía una solicitud al servidor para agregar una nueva promoción.
     *
     * @param request Cuerpo de la solicitud con los datos de la promoción a crear
     * @return Respuesta HTTP vacía (`Unit`) si la operación fue exitosa
     */
    @Headers("Content-Type: application/json")
    @POST("/")
    suspend fun agregarPromocion(@Body request: AgregarPromocionRequest): Response<Unit>
}

/**
 * Objeto singleton que configura y expone el servicio remoto para agregar promociones.
 *
 * Crea una instancia de Retrofit con la URL base del endpoint y el convertidor Gson.
 */
object ServicioRemotoAgregarPromocion {
    private const val BASE_URL = "https://agregar-promocion-819994103285.us-central1.run.app/"
    /** Instancia configurada de Retrofit utilizada para las solicitudes HTTP. */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    /** Instancia de la interfaz [ApiAgregarPromocion] para ejecutar las peticiones. */
    val api: ApiAgregarPromocion by lazy {
        retrofit.create(ApiAgregarPromocion::class.java)
    }
}
