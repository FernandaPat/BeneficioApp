package mx.mfpp.beneficioapp.model

/**
 * Representa la estructura del cuerpo de una solicitud para agregar o eliminar un favorito.
 *
 * @property id_usuario Identificador del usuario que realiza la acción
 * @property id_establecimiento Identificador del establecimiento afectado
 */
data class FavoritoRequest(
    val id_usuario: Int,
    val id_establecimiento: Int
)