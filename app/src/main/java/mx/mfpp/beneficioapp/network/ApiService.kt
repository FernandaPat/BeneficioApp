package mx.mfpp.beneficioapp.network

import mx.mfpp.beneficioapp.model.CrearCuentaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Define los servicios de red utilizados por **BeneficioApp** para comunicarse con la API REST.
 *
 * Esta interfaz contiene las definiciones de los endpoints que permiten registrar,
 * autenticar y administrar usuarios dentro del sistema. Retrofit genera automáticamente
 * la implementación en tiempo de ejecución.
 */
interface ApiService {
    /**
     * Envía una solicitud al servidor para registrar un nuevo usuario tipo **joven**.
     *
     * Este endpoint se comunica con el servicio `registerJoven` del backend,
     * enviando los datos del usuario en el cuerpo de la solicitud.
     *
     * @param request Objeto [CrearCuentaRequest] que contiene la información del nuevo usuario.
     * @return [Response] con el estado HTTP de la operación.
     *         Devuelve `Response<Void>` ya que no se espera un cuerpo de respuesta.
     */
    @POST("registerJoven")
    suspend fun registrarUsuario(@Body request: CrearCuentaRequest): Response<Void>

}
