package mx.mfpp.beneficioapp.model

data class ActualizarPromocionRequest(
    val id_promocion: Int,
    val titulo: String,
    val descripcion: String,
    val descuento: String,
    val imagen: String
)
