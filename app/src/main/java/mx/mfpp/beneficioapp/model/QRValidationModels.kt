package mx.mfpp.beneficioapp.model

// Request para validar QR
data class ValidarQRRequest(
    val token: String,
    val id_establecimiento: Int
)

// Response del endpoint validar-qr
data class QRValidationResponse(
    val success: Boolean,
    val message: String,
    val datos: QRDatos? = null
)

data class QRDatos(
    val joven: JovenValidarScanner,
    val promocion: PromocionValidarScanner,
    val token_info: TokenInfo
)

data class JovenValidarScanner(
    val id_joven: Int,
    val nombre: String,
    val curp: String?,
    val id_tarjeta: Int,
    val folio_digital: String
)

data class PromocionValidarScanner(
    val id_promocion: Int,
    val nombre: String,
    val descripcion: String?,
    val establecimiento: String
)

data class TokenInfo(
    val generado: String,
    val expira_timestamp: Long
)

