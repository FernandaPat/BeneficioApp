package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la respuesta (response) del servidor
 * tras intentar editar una promoción.
 *
 * @property mensaje Un mensaje descriptivo sobre el resultado de la operación (ej. "Promoción actualizada").
 * @property status Un código o texto que indica el estado de la respuesta (ej. "200" o "OK").
 */
data class EditarPromocionResponse(
    val mensaje: String?,
    val status: String?
)