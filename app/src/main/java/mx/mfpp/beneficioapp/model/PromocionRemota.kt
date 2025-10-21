import com.google.gson.annotations.SerializedName

data class PromocionRemota(
    @SerializedName("id") val id: Int?,
    @SerializedName("titulo") val titulo: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("descuento") val descuento: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("nombreEstablecimiento") val nombreEstablecimiento: String?,
    @SerializedName("foto") val foto: String?,
    @SerializedName("fechaExpiracion") val fechaExpiracion: String?
)
