package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una promoción usada en el historial del usuario.
 * Corresponde a la estructura que usará la UI.
 *
 * @property id El identificador único del registro de historial (canje).
 * @property idPromocion El ID de la promoción original que fue canjeada.
 * @property titulo_promocion El título de la promoción canjeada.
 * @property descripcion La descripción de la promoción (opcional).
 * @property descuento El valor o texto del descuento (ej. "20%").
 * @property fecha_canje La fecha y hora (en formato String) en que se realizó el canje.
 * @property foto_url La URL de la imagen de la promoción (opcional).
 * @property estado El estado actual del canje (ej. "Vigente", "Expirado").
 * @property establecimiento Los datos del [EstablecimientoData] donde se canjeó la promoción.
 * @property tarjeta Los datos de la [TarjetaData] con la que se canjeó (opcional).
 */
data class HistorialPromocionUsuario(
    val id: Int,
    @SerializedName("id_promocion")
    val idPromocion: Int,
    @SerializedName("titulo_promocion")
    val titulo_promocion: String,
    val descripcion: String?,
    val descuento: String,
    @SerializedName("fecha_canje")
    val fecha_canje: String,
    @SerializedName("foto_url")
    val foto_url: String?,
    val estado: String,
    val establecimiento: EstablecimientoData,
    val tarjeta: TarjetaData? = null
) {
    /** El ID del establecimiento, obtenido de [establecimiento]. */
    val id_establecimiento: Int get() = establecimiento.id
    /** El nombre del establecimiento, obtenido de [establecimiento]. */
    val nombre_establecimiento: String get() = establecimiento.nombre
}

/**
 * Modelo de datos simplificado que contiene información básica de un establecimiento,
 * usado dentro de [HistorialPromocionUsuario].
 *
 * @property id El identificador único del establecimiento.
 * @property nombre El nombre comercial del establecimiento.
 * @property categoria La categoría del establecimiento (ej. "Restaurante").
 */
data class EstablecimientoData(
    val id: Int,
    val nombre: String,
    val categoria: String
)

/**
 * Modelo de datos simplificado que contiene información de una tarjeta,
 * usado dentro de [HistorialPromocionUsuario].
 *
 * @property id El identificador único de la tarjeta.
 * @property folio El folio (número) de la tarjeta.
 */
data class TarjetaData(
    val id: Int,
    val folio: String
)

/**
 * Modelo de datos que representa la respuesta (response) del servidor
 * al solicitar el historial de un usuario (joven).
 *
 * @property success Indica si la solicitud fue exitosa.
 * @property data La lista de [HistorialPromocionUsuario] que componen el historial.
 * @property total El conteo total de canjes (opcional).
 * @property estadisticas Datos estadísticos adicionales sobre los canjes (opcional).
 */
data class HistorialJovenResponse(
    val success: Boolean,
    val data: List<HistorialPromocionUsuario>,
    val total: Int? = null,
    val estadisticas: EstadisticasData? = null
)

/**
 * Modelo de datos que agrupa estadísticas sobre el historial de canjes,
 * usado dentro de [HistorialJovenResponse].
 *
 * @property totalCanjes El número total de canjes realizados por el usuario.
 */
data class EstadisticasData(
    @SerializedName("total_canjes")
    val totalCanjes: Int
)