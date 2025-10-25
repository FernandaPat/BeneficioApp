package mx.mfpp.beneficioapp.network

import retrofit2.http.DELETE
import retrofit2.Response
import retrofit2.http.Path

/**
 * Interfaz de Retrofit que define los endpoints relacionados
 * con la gestión de promociones.
 */
interface PromocionesApi {

    /**
     * Elimina una promoción específica del servidor usando su ID.
     *
     * @param id El identificador único de la promoción que se desea eliminar.
     * @return Una [Response] de Retrofit que envuelve [Unit], indicando
     * que no se espera un cuerpo de respuesta específico (ej. HTTP 204 No Content).
     */
    @DELETE("deletePromocion/{id}")
    suspend fun eliminarPromocion(@Path("id") id: Int): Response<Unit>
}