package mx.mfpp.beneficioapp.model

data class EditarPromocionRequest(
    val id_promocion: Int,
    val titulo_promocion: String,
    val descripcion: String,
    val descuento: String?,
    val categoria: String,
    val disponible_desde: String?,
    val hasta: String?,
    val imagen: String?
)
