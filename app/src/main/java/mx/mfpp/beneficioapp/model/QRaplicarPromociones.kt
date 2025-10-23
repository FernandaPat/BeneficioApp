package mx.mfpp.beneficioapp.model
data class AplicarPromocionRequest(
    val id_tarjeta: Int,
    val id_promocion: Int,
    val id_establecimiento: Int
)

// Response del endpoint aplicar-promocion
data class AplicarPromocionResponse(
    val success: Boolean,
    val message: String,
    val registro: RegistroPromocion,
    val detalles: DetallesPromocion
)

data class RegistroPromocion(
    val id_tarjeta_promocion: Int,
    val id_tarjeta: Int,
    val id_promocion: Int,
    val fecha_uso: String,
    val estado: String
)

data class DetallesPromocion(
    val joven: String,
    val promocion: String,
    val establecimiento: String
)