package mx.mfpp.beneficioapp.model


data class Direccion(
    val calle: String = "",
    val numeroExterior: String = "",
    val numeroInterior: String = "",
    val colonia: String = "",
    val codigoPostal: String = "",
    val municipio: String = "",
    val estado: String = ""
)
