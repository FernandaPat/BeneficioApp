/**
 * Archivo: ServicioRemotoNegocioDetalle.kt
 *
 * Define un servicio remoto encargado de obtener las promociones activas
 * asociadas a un establecimiento específico.
 *
 * Utiliza Retrofit con Gson para la comunicación HTTP con el servidor.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Objeto singleton responsable de la comunicación con el servicio remoto
 * para consultar las promociones activas de un negocio en particular.
 *
 * Implementa una función suspendida para obtener los datos de manera asíncrona
 * y manejar posibles errores de red de forma segura.
 */
object ServicioRemotoNegocioDetalle {
    private const val URL_BASE = "https://listar-promociones-819994103285.us-central1.run.app/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api: PromocionJovenAPI by lazy {
        retrofit.create(PromocionJovenAPI::class.java)
    }
    /**
    * Obtiene las promociones activas de un establecimiento específico.
    *
    * Realiza una solicitud HTTP GET al endpoint remoto que devuelve
    * las promociones del establecimiento con estado "activa".
    *
    * @param establecimientoId Identificador único del establecimiento
    * @return Lista de objetos [PromocionJoven] correspondientes a las promociones activas;
    * devuelve una lista vacía en caso de error o si no existen promociones activas.
    */
    suspend fun obtenerPromocionesActivas(establecimientoId: Int): List<PromocionJoven> {
        return try {
            val resp = api.obtenerPromociones(establecimientoId = establecimientoId, estado = "activa")
            resp.data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
