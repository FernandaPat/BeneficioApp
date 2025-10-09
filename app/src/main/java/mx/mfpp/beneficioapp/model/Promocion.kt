package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

data class Promocion(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val nombre: String,

    @SerializedName("image")
    val imagenUrl: String?,

    @SerializedName("discount")
    val descuento: String?,

    @SerializedName("category")
    val categoria: String,

    @SerializedName("expiresIn")
    val expiraEn: Int?,

    @SerializedName("location")
    val ubicacion: String?,

    @SerializedName("isFavorite")
    val esFavorito: Boolean,

    @SerializedName("rating")
    val rating: Double?,

    @SerializedName("description")
    val descripcion: String?

) {
    fun obtenerTextoExpiracion(): String {
        return when (expiraEn) {
            null -> "Sin fecha"
            0 -> "Expira hoy"
            1 -> "Expira mañana"
            in 2..7 -> "Expira en $expiraEn días"
            else -> " Válido por $expiraEn días"
        }
    }

    fun obtenerRatingTexto(): String {
        return rating?.let { "⭐ ${"%.1f".format(it)}" } ?: "⭐ --"
    }
}
