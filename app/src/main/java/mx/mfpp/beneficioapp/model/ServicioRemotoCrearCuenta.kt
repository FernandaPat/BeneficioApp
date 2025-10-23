/**
 * Archivo: ServicioRemotoCrearCuenta.kt
 *
 * Define la interfaz y el servicio remoto para registrar un nuevo usuario joven
 * mediante una solicitud HTTP POST al backend.
 *
 * Utiliza Retrofit con conversión JSON mediante Gson.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
/**
 * Interfaz Retrofit que define el endpoint para registrar un nuevo usuario joven.
 *
 * Envía los datos del formulario de registro en formato JSON mediante una solicitud POST.
 */
interface ApiCrearCuenta {
    /**
     * Envía una solicitud al servidor para registrar un nuevo usuario joven.
     *
     * @param request Cuerpo de la solicitud con los datos personales y de acceso del usuario
     * @return Respuesta HTTP que puede contener información del nuevo usuario o un mensaje de estado
     */
    @POST("registroJoven")
    suspend fun registrarUsuario(@Body request: CrearCuentaRequest): Response<Any>
}

/**
 * Objeto singleton que configura y expone el servicio remoto para registrar usuarios jóvenes.
 *
 * Crea una instancia de Retrofit con la URL base del servicio y el convertidor Gson.
 */
object ServicioRemotoCrearCuenta {

    private const val BASE_URL = "https://registrojoven-819994103285.us-central1.run.app/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiCrearCuenta by lazy {
        retrofit.create(ApiCrearCuenta::class.java)
    }
}
