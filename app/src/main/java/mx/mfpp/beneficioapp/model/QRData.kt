package mx.mfpp.beneficioapp.model

data class PromocionData(
    val numeroTarjeta: String,
    val fecha: String,
    val nombrePromocion: String
)

// BORRAR ARRIBA CUANDO SE NECECITE LA PROMOCION DATA

// Request para generar QR
data class GenerarQRRequest(
    val id_joven: Int,
    val id_promocion: Int
)

// Preview que devuelve la API
data class QRPreview(
    val joven: String,
    val folio_digital: String,
    val promocion: String,
    val descripcion: String,
    val establecimiento: String
)

// Response completa de la API
data class QRTokenResponse(
    val success: Boolean,
    val token: String,
    val expira_en: String,
    val expira_timestamp: Long,
    val preview: QRPreview
)
