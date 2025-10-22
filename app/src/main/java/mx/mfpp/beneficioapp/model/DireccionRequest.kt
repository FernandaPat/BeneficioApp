package mx.mfpp.beneficioapp.model

data class DireccionRequest(
    val calle: String,
    val numero_exterior: String,
    val numero_interior: String?,
    val colonia: String,
    val codigo_postal: String,
    val municipio: String,
    val estado: String
)