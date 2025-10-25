import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa la estructura de una promoción
 * tal como se recibe desde una fuente remota (ej. API),
 * utilizando [SerializedName] para el mapeo con Gson.
 * Todas las propiedades son nulas para manejar respuestas incompletas.
 *
 * @property id El identificador único de la promoción (puede ser nulo).
 * @property titulo El título de la promoción (puede ser nulo).
 * @property descripcion La descripción detallada de la promoción (puede ser nulo).
 * @property descuento El texto o valor del descuento (puede ser nulo).
 * @property estado El estado actual de la promoción (ej. "Activa") (puede ser nulo).
 * @property nombreEstablecimiento El nombre del negocio que ofrece la promoción (puede ser nulo).
 * @property foto La URL de la imagen de la promoción (puede ser nulo).
 * @property fechaExpiracion La fecha (en formato String) de expiración (puede ser nulo).
 */
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