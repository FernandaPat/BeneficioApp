/**
 * Archivo: DireccionRequest.kt
 *
 * Define el modelo de datos utilizado para enviar la información
 * de dirección del usuario al servidor como parte de una solicitud.
 *
 * Generalmente se incluye dentro de otros modelos como [CrearCuentaRequest].
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa la estructura de una dirección dentro de una solicitud al servidor.
 *
 * Contiene los datos básicos necesarios para identificar la ubicación
 * del usuario o establecimiento.
 *
 * @property calle Nombre de la calle
 * @property numero_exterior Número exterior del domicilio
 * @property numero_interior Número interior (opcional)
 * @property colonia Colonia o barrio del domicilio
 * @property codigo_postal Código postal
 * @property municipio Municipio o alcaldía
 * @property estado Estado o entidad federativa
 */
data class DireccionRequest(
    val calle: String,
    val numero_exterior: String,
    val numero_interior: String?,
    val colonia: String,
    val codigo_postal: String,
    val municipio: String,
    val estado: String
)