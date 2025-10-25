package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la respuesta (response) del servidor
 * al solicitar agregar nuevas promociones.
 *
 * @property data La lista de promociones (posiblemente las recién agregadas
 * o la lista actualizada tras la inserción).
 */
data class AgregarPromocionesResponse(
    val data: List<Promocion>
)