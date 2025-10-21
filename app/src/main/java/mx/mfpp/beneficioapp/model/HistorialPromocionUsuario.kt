package mx.mfpp.beneficioapp.model

/**
 * Representa una promoción usada en el historial del usuario.
 * Corresponde a la estructura del JSON de la API de historial.
 *
 * @property id Identificador único del registro de historial
 * @property titulo_promocion Título de la promoción usada
 * @property descripcion Descripción de la promoción
 * @property fecha_uso Fecha en que se usó la promoción
 * @property foto URL de la imagen de la promoción
 * @property estado Estado de la promoción ("usada", "activa", etc.)
 * @property id_establecimiento ID del establecimiento
 * @property nombre_establecimiento Nombre del establecimiento
 */
data class HistorialPromocionUsuario(
    val id: Int,
    val titulo_promocion: String,
    val descripcion: String,
    val fecha_uso: String,
    val foto: String?,
    val estado: String,
    val id_establecimiento: Int,
    val nombre_establecimiento: String
)

/**
 * Respuesta de la API de historial con paginación
 */
data class HistorialResponse(
    val data: List<HistorialPromocionUsuario>,
    val pagination: HistorialPagination
)

/**
 * Información de paginación para historial
 */
data class HistorialPagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val total_pages: Int,
    val has_next: Boolean,
    val has_prev: Boolean
)