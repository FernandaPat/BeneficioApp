package mx.mfpp.beneficioapp.model
data class AgregarPromocionRequest(
    val id_negocio: Int,
    val titulo: String?,
    val descripcion: String?,
    val descuento: String?,
    val disponible_desde: String?,
    val hasta: String?,
    val imagen: String?
)

