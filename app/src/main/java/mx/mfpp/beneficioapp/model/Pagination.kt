package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la información de paginación
 * devuelta por una API en respuestas de listas.
 *
 * @property current_page El número de la página actual que se está devolviendo.
 * @property total_pages El número total de páginas disponibles.
 * @property total_items El conteo total de ítems en todos las páginas.
 * @property has_next Indica (true/false) si existe una página siguiente.
 * @property has_prev Indica (true/false) si existe una página anterior.
 * @property next_page El número de la página siguiente (nulo si [has_next] es false).
 * @property prev_page El número de la página anterior (nulo si [has_prev] es false).
 */
data class Pagination(
    val current_page: Int,
    val total_pages: Int,
    val total_items: Int,
    val has_next: Boolean,
    val has_prev: Boolean,
    val next_page: Int?,
    val prev_page: Int?
)