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
    val descuento: Double,
    @SerializedName("fecha_canje")
    val fecha_canje: String,
    @SerializedName("foto_url")
    val foto_url: String?,
    val estado: String,
    val establecimiento: EstablecimientoData
) {
    val id_establecimiento: Int get() = establecimiento.id
    val nombre_establecimiento: String get() = establecimiento.nombre
}

data class EstablecimientoData(
    val id: Int,
    val nombre: String,
    val categoria: String
)

data class HistorialJovenResponse(
    val success: Boolean,
    val data: List<HistorialPromocionUsuario>
)
