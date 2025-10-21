// mx.mfpp.beneficioapp.model.Establecimiento
package mx.mfpp.beneficioapp.model

/**
 * Representa un establecimiento comercial en la aplicación.
 * Corresponde a la estructura del JSON de la API.
 *
 * @property id_establecimiento Identificador único del establecimiento
 * @property nombre Nombre comercial del establecimiento
 * @property foto URL de la imagen del establecimiento
 * @property colonia Colonia donde se encuentra el establecimiento
 * @property latitud Coordenada de latitud (puede ser null)
 * @property longitud Coordenada de longitud (puede ser null)
 * @property id_categoria Identificador de la categoría
 * @property nombre_categoria Nombre de la categoría
 */
data class Establecimiento(
    val id_establecimiento: Int,
    val nombre: String,
    val colonia: String,
    val nombre_categoria: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val imagen: String? = null,
    val es_favorito: Boolean = false
)


/**
 * Respuesta de la API de establecimientos con paginación
 */
data class EstablecimientosResponse(
    val data: List<Establecimiento>,
    val pagination: EstablecimientoPagination
)

/**
 * Información de paginación para establecimientos
 */
data class EstablecimientoPagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val total_pages: Int,
    val has_next: Boolean,
    val has_prev: Boolean
)