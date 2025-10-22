package mx.mfpp.beneficioapp.model

/**
 * Representa la dirección física de un usuario dentro del sistema Beneficio Joven.
 *
 * @property calle Nombre de la calle donde reside el usuario.
 * @property numeroExterior Número exterior del domicilio.
 * @property numeroInterior Número interior del domicilio, si aplica.
 * @property colonia Colonia o barrio del domicilio.
 * @property codigoPostal Código postal correspondiente a la ubicación.
 * @property municipio Municipio o delegación donde se encuentra la dirección.
 * @property estado Estado o entidad federativa.
 */


data class Direccion(
    val calle: String = "",
    val numeroExterior: String = "",
    val numeroInterior: String = "",
    val colonia: String = "",
    val codigoPostal: String = "",
    val municipio: String = "",
    val estado: String = ""
)
