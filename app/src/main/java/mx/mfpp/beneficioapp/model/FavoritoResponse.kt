package mx.mfpp.beneficioapp.model
/**
 * Representa la respuesta del servidor al agregar o eliminar un favorito.
 *
 * @property message Mensaje informativo del resultado
 * @property id_favorito Identificador del registro de favorito (opcional)
 * @property id_usuario Identificador del usuario relacionado
 * @property id_establecimiento Identificador del establecimiento relacionado
 */
data class FavoritoResponse(
    val message: String,
    val id_favorito: Int? = null,
    val id_usuario: Int,
    val id_establecimiento: Int
)