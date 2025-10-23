/**
 * Archivo: PromocionesResponse.kt
 *
 * Define el modelo de datos que representa la respuesta general del backend
 * al consultar una lista de promociones.
 *
 * Este modelo se utiliza para mapear la estructura JSON devuelta por la API,
 * donde se incluye el estado de la operación y la lista de promociones remotas.
 */
package mx.mfpp.beneficioapp.model

import PromocionRemota
import com.google.gson.annotations.SerializedName

/**
 * Representa la estructura de la respuesta del servidor al obtener promociones.
 *
 * Contiene el estado de la operación y la lista de objetos [PromocionRemota]
 * devueltos por la API.
 *
 * @property status Estado de la operación (por ejemplo, "success" o "error")
 * @property data Lista de promociones obtenidas desde el servidor (opcional)
 */
data class PromocionesResponse(
    val status: String?,
    val data: List<PromocionRemota>?
)