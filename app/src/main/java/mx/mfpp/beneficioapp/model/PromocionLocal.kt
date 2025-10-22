package mx.mfpp.beneficioapp.model

data class PromocionLocal(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val descuento: String,
    val categoria: String,
    val ubicacion: String,
    val imagenUrl: String,
    val expiraEn: String?,
    val esFavorito: Boolean,
    val rating: Double?
)
