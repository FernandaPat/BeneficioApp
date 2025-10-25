package mx.mfpp.beneficioapp.model

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de Retrofit que define los endpoints para obtener
 * promociones desde la perspectiva de un usuario (joven).
 */
interface PromocionJovenAPI {

    /**
     * Obtiene una lista paginada de promociones, con filtros opcionales.
     *
     * @param page El número de página que se desea obtener (default: 1).
     * @param limit La cantidad de promociones por página (default: 50).
     * @param establecimientoId El ID (opcional) de un establecimiento específico
     * para filtrar las promociones.
     * @param estado El estado (opcional) de las promociones a filtrar (ej. "Activa").
     * @return Un objeto [PromocionesJovenResponse] que contiene la lista de
     * promociones y la información de paginación.
     */
    @GET("promociones")
    suspend fun obtenerPromociones(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("establecimiento_id") establecimientoId: Int? = null,
        @Query("estado") estado: String? = null
    ): PromocionesJovenResponse
}