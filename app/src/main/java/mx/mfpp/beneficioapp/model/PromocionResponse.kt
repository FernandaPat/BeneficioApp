package mx.mfpp.beneficioapp.model

import PromocionRemota
import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa la respuesta (response) general del backend
 * al solicitar una lista de promociones.
 *
 * @property status Un código o texto que indica el estado de la respuesta (ej. "success").
 * @property data La lista de [PromocionRemota] (promociones remotas)
 * incluidas en la respuesta (puede ser nula).
 */
data class PromocionesResponse(
    val status: String?,
    val data: List<PromocionRemota>?
)