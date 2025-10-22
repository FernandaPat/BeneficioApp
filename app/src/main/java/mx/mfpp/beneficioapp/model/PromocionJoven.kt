package mx.mfpp.beneficioapp.model


data class PromocionJoven(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val fecha_creacion: String,
    val fecha_expiracion: String,
    val foto: String?,
    val estado: String,
    val id_establecimiento: Int,
    val nombre_establecimiento: String,
    val descuento: String
)

data class PromocionesJovenResponse(
    val data: List<PromocionJoven>,
    val pagination: Pagination
)
