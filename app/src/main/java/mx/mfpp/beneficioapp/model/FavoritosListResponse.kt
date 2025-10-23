package mx.mfpp.beneficioapp.model
/**
 * Representa la respuesta del servidor al obtener la lista de favoritos de un usuario.
 *
 * @property favoritos Lista de favoritos con su información detallada
 * @property total Número total de favoritos registrados
 */
data class FavoritosListResponse(
    val favoritos: List<FavoritoDetalle>,
    val total: Int
)