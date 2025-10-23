package mx.mfpp.beneficioapp.model
/**
 * Representa la información detallada de un establecimiento marcado como favorito.
 *
 * @property id_favoritos Identificador único del registro de favorito
 * @property id_establecimiento Identificador del establecimiento asociado
 * @property nombre_establecimiento Nombre del establecimiento
 * @property foto URL de la imagen del establecimiento (opcional)
 * @property colonia Colonia o ubicación aproximada del establecimiento (opcional)
 * @property nombre_categoria Categoría del establecimiento (opcional)
 */
data class FavoritoDetalle(
    val id_favoritos: Int,
    val id_establecimiento: Int,
    val nombre_establecimiento: String,
    val foto: String?,
    val colonia: String?,
    val nombre_categoria: String?
)