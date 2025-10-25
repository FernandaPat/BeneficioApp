package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la solicitud (request) para definir
 * o actualizar una dirección.
 *
 * @property calle El nombre de la calle.
 * @property numero_exterior El número exterior de la propiedad.
 * @property numero_interior El número interior (departamento, oficina, etc.), puede ser nulo.
 * @property colonia El nombre de la colonia o vecindario.
 * @property codigo_postal El código postal de la zona.
 * @property municipio El municipio o alcaldía.
 * @property estado El estado o entidad federativa.
 */
data class DireccionRequest(
    val calle: String,
    val numero_exterior: String,
    val numero_interior: String?,
    val colonia:String,
    val codigo_postal: String,
    val municipio: String,
    val estado: String
)