package mx.mfpp.beneficioapp.model

data class Negocio(
    val id: Int,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val categoria: String
)
