package mx.mfpp.beneficioapp.model

data class RegistroJovenRequest(
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val curp: String,
    val fecha_nacimiento: String,
    val genero: String,
    val correo: String,
    val telefono: String,
    val contrasena: String,
    val direccion: DireccionRequest
)