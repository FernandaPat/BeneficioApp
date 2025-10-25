package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa una promoción, adaptado para uso local
 * (posiblemente en la capa de UI o en una caché).
 *
 * @property id El identificador único de la promoción.
 * @property nombre El título o nombre de la promoción.
 * @property descripcion Una descripción detallada de la promoción.
 * @property descuento El texto o valor del descuento (ej. "20%", "2x1").
 * @property categoria La categoría a la que pertenece la promoción (ej. "Restaurante").
 * @property ubicacion Texto descriptivo de la ubicación del negocio.
 * @property imagenUrl La URL de la imagen principal de la promoción.
 * @property expiraEn Texto descriptivo sobre el vencimiento (ej. "Vence en 2 días"), puede ser nulo.
 * @property esFavorito Indica si el usuario actual ha marcado esta promoción como favorita.
 * @property rating La calificación (rating) de la promoción, puede ser nula.
 */
data class PromocionLocal(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val descuento: String,
    val categoria: String,
    val ubicacion: String,
    val imagenUrl: String,
    val expiraEn: String?,
    val esFavorito: Boolean,
    val rating: Double?
)