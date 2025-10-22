package mx.mfpp.beneficioapp.model

data class PromocionRequest(
    val id_negocio: Int,        // ID del negocio que publica la promo
    val titulo_promocion: String, // Título o nombre de la promoción
    val descripcion: String,      // Descripción breve
    val descuento: String?,       // Ejemplo: "15%" o "2x1"
    val categoria: String,        // Ejemplo: "Comida", "Belleza"
    val ubicacion: String?,       // Dirección o ubicación opcional
    val fecha_expiracion: String? // Fecha de expiración (opcional, formato ISO "2025-12-31")
)
