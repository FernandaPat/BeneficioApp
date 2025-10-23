/**
 * Archivo: PromocionJovenAPI.kt
 *
 * Define la interfaz de comunicación con el endpoint de promociones disponibles
 * para los usuarios jóvenes mediante Retrofit.
 *
 * Permite obtener promociones filtradas por establecimiento, estado o mediante paginación.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.http.GET
import retrofit2.http.Query
/**
 * Interfaz que define las operaciones HTTP relacionadas con las promociones visibles para jóvenes.
 *
 * Utiliza Retrofit para realizar solicitudes GET al recurso "promociones".
 */
interface PromocionJovenAPI {
    /**
     * Obtiene una lista de promociones disponibles para los usuarios jóvenes.
     *
     * Puede filtrarse por página, cantidad de resultados, establecimiento o estado.
     *
     * @param page Número de la página de resultados (por defecto 1)
     * @param limit Número máximo de promociones por página (por defecto 50)
     * @param establecimientoId Identificador del establecimiento para filtrar promociones (opcional)
     * @param estado Filtro de estado de la promoción (por ejemplo, "activa", "expirada") (opcional)
     * @return Un objeto [PromocionesJovenResponse] con la lista de promociones y metadatos de paginación
     */
    @GET("promociones") // Asegúrate de que este endpoint sea correcto para la nueva API
    suspend fun obtenerPromociones(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("establecimiento_id") establecimientoId: Int? = null,
        @Query("estado") estado: String? = null
    ): PromocionesJovenResponse
}