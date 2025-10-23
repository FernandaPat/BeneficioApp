package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una promoción usada en el historial del usuario.
 * Corresponde a la estructura que usará la UI.
 */
data class HistorialPromocionUsuario(
    val id: Int,
    @SerializedName("id_promocion")
    val idPromocion: Int,
    @SerializedName("titulo_promocion")
    val titulo_promocion: String,
    val descripcion: String?,
    val descuento: String, // ✅ CAMBIAR de Double a String
    @SerializedName("fecha_canje")
    val fecha_canje: String,
    @SerializedName("foto_url")
    val foto_url: String?,
    val estado: String,
    val establecimiento: EstablecimientoData,
    val tarjeta: TarjetaData? = null // ✅ AGREGAR campo tarjeta
) {
    val id_establecimiento: Int get() = establecimiento.id
    val nombre_establecimiento: String get() = establecimiento.nombre
}

data class EstablecimientoData(
    val id: Int,
    val nombre: String,
    val categoria: String
)

data class TarjetaData(
    val id: Int,
    val folio: String
)

data class HistorialJovenResponse(
    val success: Boolean,
    val data: List<HistorialPromocionUsuario>,
    val total: Int? = null, // ✅ AGREGAR campos adicionales
    val estadisticas: EstadisticasData? = null
)

data class EstadisticasData(
    @SerializedName("total_canjes")
    val totalCanjes: Int
)