package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto (Singleton) que gestiona la comunicación con la API remota
 * (Cloud Function) para obtener los detalles de las promociones
 * de un negocio específico.
 *
 * Reutiliza la [PromocionJovenAPI] pero la consume de manera filtrada.
 */
object ServicioRemotoNegocioDetalle {

    /** URL base del servicio (Cloud Function) para listar promociones. */
    private const val URL_BASE = "https://listar-promociones-819994103285.us-central1.run.app/"

    /**
     * Instancia [Retrofit] configurada con [URL_BASE] y [GsonConverterFactory].
     * Se inicializa de forma perezosa (lazy).
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Instancia de la [PromocionJovenAPI] (servicio de Retrofit) creada a partir de [retrofit].
     * Se inicializa de forma perezosa (lazy).
     */
    private val api: PromocionJovenAPI by lazy {
        retrofit.create(PromocionJovenAPI::class.java)
    }

    /**
     * Obtiene únicamente las promociones "activas" para un establecimiento específico.
     *
     * Realiza una llamada a [PromocionJovenAPI.obtenerPromociones] pero aplica
     * los filtros `establecimientoId` y `estado = "activa"`.
     *
     * @param establecimientoId El ID del negocio cuyas promociones activas se desean obtener.
     * @return Una [List] de [PromocionJoven] que están activas. Retorna una
     * lista vacía si ocurre cualquier excepción o si no se encuentran resultados.
     */
    suspend fun obtenerPromocionesActivas(establecimientoId: Int): List<PromocionJoven> {
        return try {
            // Llama a la API filtrando por ID de establecimiento y estado "activa"
            val resp = api.obtenerPromociones(establecimientoId = establecimientoId, estado = "activa")
            resp.data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}