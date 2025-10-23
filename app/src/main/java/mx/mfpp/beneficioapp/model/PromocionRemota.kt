/**
 * Archivo: PromocionRemota.kt
 *
 * Define el modelo de datos que representa una promoción obtenida directamente
 * desde el servidor remoto mediante una solicitud HTTP.
 *
 * Esta clase se utiliza para mapear la respuesta JSON del backend
 * antes de transformarla a un modelo local o de dominio.
 */
import com.google.gson.annotations.SerializedName
/**
 * Representa una promoción tal como se recibe desde la API remota.
 *
 * Contiene los campos principales devueltos por el servidor, que pueden
 * diferir ligeramente en nombre o estructura de los modelos locales.
 *
 * @property id Identificador único de la promoción (opcional)
 * @property titulo Título o nombre descriptivo de la promoción (opcional)
 * @property descripcion Descripción detallada de la promoción (opcional)
 * @property descuento Valor o porcentaje de descuento (opcional)
 * @property estado Estado actual de la promoción (por ejemplo, "Activa", "Expirada") (opcional)
 * @property nombreEstablecimiento Nombre del establecimiento que ofrece la promoción (opcional)
 * @property foto URL o cadena codificada de la imagen asociada (opcional)
 * @property fechaExpiracion Fecha de expiración o fin de validez de la promoción (opcional)
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
