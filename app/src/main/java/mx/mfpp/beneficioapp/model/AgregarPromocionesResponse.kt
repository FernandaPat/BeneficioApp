/**
 * Archivo: AgregarPromocionesResponse.kt
 *
 * Define el modelo de datos para la respuesta recibida al agregar
 * una o varias promociones en el servidor.
 *
 * Este modelo se utiliza principalmente en la capa de red
 * (por ejemplo, al realizar una petición POST mediante Retrofit)
 * para mapear la respuesta JSON a un objeto Kotlin.
 *
 * Autor: [Tu nombre o equipo]
 * Fecha de creación: [fecha]
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura de la respuesta del servidor
 * al agregar una o varias promociones nuevas.
 *
 * @property data Lista de promociones creadas o actualizadas devueltas por el servidor
 */
data class AgregarPromocionesResponse(
    val data: List<Promocion>
)
