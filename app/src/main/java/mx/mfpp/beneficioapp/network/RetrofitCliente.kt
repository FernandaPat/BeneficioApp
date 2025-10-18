package mx.mfpp.beneficioapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Objeto responsable de configurar y proporcionar la instancia de Retrofit
 * utilizada para las comunicaciones HTTP en **BeneficioApp**.
 *
 * Este cliente se encarga de inicializar Retrofit con la URL base de la API
 * y el convertidor de datos **Gson**, que permite serializar y deserializar
 * objetos JSON de forma automática.
 *
 * Se implementa como un `object` (singleton) para garantizar que exista
 * una sola instancia compartida de Retrofit durante toda la aplicación.
 */
object RetrofitClient {
    /** URL base del endpoint de la API desplegada en AWS API Gateway. */
    private const val BASE_URL = "https://9somwbyil5.execute-api.us-east-1.amazonaws.com/prod/"

    /**
     * Instancia perezosa (`lazy`) de [ApiService], encargada de definir
     * los endpoints disponibles en el servidor.
     *
     * @see ApiService
     */
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
