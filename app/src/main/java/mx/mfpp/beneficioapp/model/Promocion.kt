/**
 * Archivo: Promocion.kt
 *
 * Define el modelo de datos que representa una promoción o descuento disponible
 * dentro de la aplicación.
 *
 * Este modelo se utiliza para mostrar información en la interfaz de usuario
 * y para mapear las respuestas JSON recibidas desde el servidor.
 */
package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una promoción o oferta disponible dentro del sistema.
 *
 * Contiene los datos principales relacionados con una promoción activa,
 * incluyendo detalles, fechas, categoría, imagen y estado de favorito.
 *
 * @property id Identificador único de la promoción
 * @property nombre Nombre o título de la promoción
 * @property descripcion Descripción detallada de la promoción
 * @property descuento Valor o porcentaje del descuento (por ejemplo, "15%")
 * @property desde Fecha de inicio de la promoción (opcional)
 * @property hasta Fecha de finalización de la promoción (opcional)
 * @property categoria Categoría o tipo de promoción (por ejemplo, "Comida", "Belleza")
 * @property expiraEn Texto informativo sobre el tiempo restante antes de expirar
 * @property esFavorito Indica si la promoción está marcada como favorita por el usuario
 * @property rating Valoración promedio de la promoción
 * @property ubicacion Ubicación o zona del establecimiento asociado
 * @property imagenUrl URL de la imagen asociada a la promoción
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



