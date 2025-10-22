package mx.mfpp.beneficioapp.model

data class CrearCuentaRequest(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val curp: String = "",
    val genero: String = "",
    val correo: String = "",
    val celular: String = "",
    val password: String = "",
    val fechaNacimiento: String = "",
    val direccion: Direccion = Direccion(),
    val folio_antiguo: String? = null,
    val tieneTarjeta: Boolean? = null,
    val consentimientoAceptado: Boolean = false
)
