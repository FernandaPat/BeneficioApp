
/**
 * Archivo: Pagination.kt
 *
 * Define el modelo de datos utilizado para representar la información de paginación
 * en las respuestas del servidor que devuelven listas de elementos.
 *
 * Permite gestionar la navegación entre páginas de resultados y conocer
 * el total de registros disponibles.
 */package mx.mfpp.beneficioapp.model

/**
 * Representa los metadatos de paginación asociados a una respuesta de lista.
 *
 * @property current_page Número de la página actual
 * @property total_pages Número total de páginas disponibles
 * @property total_items Número total de elementos registrados
 * @property has_next Indica si existe una página siguiente
 * @property has_prev Indica si existe una página anterior
 * @property next_page Número de la siguiente página (opcional)
 * @property prev_page Número de la página anterior (opcional)
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
