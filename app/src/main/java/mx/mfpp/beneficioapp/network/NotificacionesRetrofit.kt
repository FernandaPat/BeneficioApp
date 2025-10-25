package mx.mfpp.beneficioapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto (Singleton) que proporciona una instancia de Retrofit
 * configurada para el servicio remoto de registro de notificaciones.
 *
 * Expone la instancia de [NotificacionesApi] lista para ser usada.
 */
object NotificacionesRetrofit {
    /** URL base del servicio (Cloud Function) para registrar tokens. */
    private const val BASE_URL = "https://registrar-token-819994103285.us-central1.run.app"

    /**
     * Instancia pública de [NotificacionesApi], creada por Retrofit y
     * configurada con [BASE_URL].
     * Se inicializa de forma perezosa (lazy).
     */
    val api: NotificacionesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificacionesApi::class.java)
    }
}