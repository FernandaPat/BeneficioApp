/**
 * Archivo: ServicioRemotoEliminarPromocion.kt
 *
 * Define la interfaz y el servicio remoto para eliminar una promoción existente
 * en el servidor mediante una solicitud HTTP DELETE.
 *
 * Utiliza Retrofit con conversión JSON mediante Gson.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.Response
/**
 * Interfaz Retrofit que define el endpoint para eliminar una promoción.
 *
 * Envía una solicitud DELETE al servidor especificando el identificador de la promoción.
 */
interface ApiEliminarPromocion {
    /**
     * Elimina una promoción del servidor según su identificador único.
     *
     * @param idPromocion Identificador de la promoción a eliminar
     * @return Respuesta HTTP sin cuerpo (`Unit`) si la operación fue exitosa
     */
    @DELETE("eliminarPromocion/{id_promocion}")
    suspend fun eliminarPromocion(@Path("id_promocion") idPromocion: Int): Response<Unit>
}
/**
 * Objeto singleton que configura y expone el servicio remoto para eliminar promociones.
 *
 * Crea una instancia de Retrofit con la URL base correspondiente al endpoint
 * de eliminación de promociones y el convertidor Gson.
 */
object ServicioRemotoEliminarPromocion {
    private const val BASE_URL = "https://eliminar-promocion-819994103285.us-central1.run.app/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiEliminarPromocion by lazy {
        retrofit.create(ApiEliminarPromocion::class.java)
    }
}
