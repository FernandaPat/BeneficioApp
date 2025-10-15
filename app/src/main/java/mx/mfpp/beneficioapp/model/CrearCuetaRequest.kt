package mx.mfpp.beneficioapp.model

data class CrearCuentaRequest(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val fechaNacimiento: String = "",
    val genero: String = "",
    val curp: String = "",
    val correo: String = "",
    val celular: String = "",
    val password: String = "",
    val folio_antiguo: String? = null,
    val tieneTarjeta: Boolean? = null,
    val direccion: Direccion = Direccion(),
    val consentimientoAceptado: Boolean = false
)
