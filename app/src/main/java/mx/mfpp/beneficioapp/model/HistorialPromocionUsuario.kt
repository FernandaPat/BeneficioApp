/**
 * Archivo: HistorialPromocionUsuario.kt
 *
 * Define los modelos de datos relacionados con el historial de promociones canjeadas
 * por un usuario joven. Incluye la estructura principal de la respuesta del servidor,
 * así como los objetos anidados de establecimientos, tarjetas y estadísticas.
 */
package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una promoción canjeada por el usuario dentro de su historial.
 *
 * Corresponde a la estructura de datos que se mostrará en la interfaz de usuario.
 *
 * @property id Identificador único del registro en el historial
 * @property idPromocion Identificador de la promoción canjeada
 * @property titulo_promocion Título de la promoción
 * @property descripcion Descripción de la promoción (opcional)
 * @property descuento Valor o porcentaje de descuento (en texto, por ejemplo "20%")
 * @property fecha_canje Fecha en la que se realizó el canje
 * @property foto_url URL de la imagen asociada a la promoción (opcional)
 * @property estado Estado actual del canje (por ejemplo, "canjeado" o "vencido")
 * @property establecimiento Información del establecimiento asociado a la promoción
 * @property tarjeta Datos de la tarjeta usada en el canje (opcional)
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
    /** Identificador del establecimiento asociado a la promoción. */
    val id_establecimiento: Int get() = establecimiento.id
    /** Nombre del establecimiento asociado a la promoción. */
    val nombre_establecimiento: String get() = establecimiento.nombre
}
/**
 * Representa los datos básicos de un establecimiento asociados a un canje.
 *
 * @property id Identificador del establecimiento
 * @property nombre Nombre del establecimiento
 * @property categoria Categoría a la que pertenece el establecimiento
 */
data class EstablecimientoData(
    val id: Int,
    val nombre: String,
    val categoria: String
)
/**
 * Representa los datos de la tarjeta usada para realizar un canje.
 *
 * @property id Identificador de la tarjeta
 * @property folio Folio único asignado a la tarjeta
 */
data class TarjetaData(
    val id: Int,
    val folio: String
)
/**
 * Representa la respuesta completa del servidor al obtener el historial de canjes de un usuario.
 *
 * @property success Indica si la operación fue exitosa
 * @property data Lista de promociones canjeadas por el usuario
 * @property total Número total de registros en el historial (opcional)
 * @property estadisticas Información agregada con métricas del historial (opcional)
 */
data class HistorialJovenResponse(
    val success: Boolean,
    val data: List<HistorialPromocionUsuario>,
    val total: Int? = null,
    val estadisticas: EstadisticasData? = null
)
/**
 * Representa estadísticas generales del historial de canjes.
 *
 * @property totalCanjes Número total de promociones canjeadas por el usuario
 */
data class EstadisticasData(
    @SerializedName("total_canjes")
    val totalCanjes: Int
)