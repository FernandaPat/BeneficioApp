/**
 * Archivo: ServicioRemotoPromociones.kt
 *
 * Define el servicio remoto encargado de obtener las promociones
 * asociadas a un negocio específico desde el servidor.
 *
 * Utiliza Retrofit con conversión JSON mediante Gson para manejar
 * las solicitudes HTTP de forma asíncrona y tipada.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
/**
 * Interfaz que define los endpoints disponibles para la obtención
 * de promociones desde el backend.
 *
 * La solicitud se realiza con Retrofit utilizando un método HTTP GET.
 */
interface ApiPromociones {
    @GET("/")
    suspend fun getPromocionesPorNegocio(
        @Query("establecimiento_id") establecimientoId: Int
    ): Response<PromocionesResponse>
}
/**
 * Objeto singleton que configura y expone el servicio remoto
 * para interactuar con el endpoint de promociones.
 *
 * Contiene la instancia de Retrofit y el cliente API ya inicializado.
 */
object ServicioRemotoPromociones {
    private const val BASE_URL = "https://listar-promociones-819994103285.us-central1.run.app/"

    val api: ApiPromociones by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiPromociones::class.java)
    }
}

