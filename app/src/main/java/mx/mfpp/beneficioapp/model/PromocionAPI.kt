/**
 * Archivo: PromocionesApi.kt
 *
 * Define la interfaz de comunicación con el endpoint de promociones
 * mediante Retrofit.
 *
 * Permite eliminar una promoción existente en el servidor
 * a través de una solicitud HTTP DELETE.
 */
package mx.mfpp.beneficioapp.network

import retrofit2.http.DELETE
import retrofit2.Response
import retrofit2.http.Path

/**
 * Interfaz que define las operaciones HTTP relacionadas con las promociones.
 *
 * Utiliza Retrofit para realizar solicitudes DELETE al recurso "deletePromocion".
 */
interface PromocionesApi {
    /**
     * Elimina una promoción existente del servidor según su identificador.
     *
     * @param id Identificador único de la promoción a eliminar
     * @return Respuesta HTTP sin contenido (`Unit`) si la operación fue exitosa
     */
    @DELETE("deletePromocion/{id}")
    suspend fun eliminarPromocion(@Path("id") id: Int): Response<Unit> // Respuesta vacía si no es necesario retornar nada
}
