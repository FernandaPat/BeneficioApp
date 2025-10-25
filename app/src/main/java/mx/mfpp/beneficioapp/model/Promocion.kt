package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una promoción o oferta disponible en la aplicación.
 *
 * @property id El identificador único de la promoción.
 * @property nombre El título o nombre de la promoción.
 * @property descripcion Una descripción detallada de la promoción.
 * @property descuento El texto o valor del descuento (ej. "20%", "2x1").
 * @property desde La fecha (en formato String) de inicio de vigencia (default: "").
 * @property hasta La fecha (en formato String) de fin de vigencia (default: "").
 * @property categoria La categoría a la que pertenece la promoción (default: "").
 * @property expiraEn Texto descriptivo sobre el vencimiento (ej. "Expira en 3 días") (default: "").
 * @property esFavorito Indica si el usuario actual ha marcado esta promoción como favorita (default: false).
 * @property rating La calificación promedio de la promoción (default: 0f).
 * @property ubicacion Texto descriptivo de la ubicación del negocio (ej. "A 2km") (default: "").
 * @property imagenUrl La URL de la imagen principal de la promoción (default: "").
 */
data class Promocion(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val descuento: String,
    val desde: String = "",
    val hasta: String = "",
    val categoria: String = "",
    val expiraEn: String = "",
    val esFavorito: Boolean = false,
    val rating: Float = 0f,
    val ubicacion: String = "",
    val imagenUrl: String = ""
)