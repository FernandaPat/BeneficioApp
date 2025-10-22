package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una promoción o oferta disponible en la aplicación.
 */
data class Promocion(
    val id: Int,
    val nombre: String,
    val descripcion: String?,
    val descuento: String?,
    val categoria: String,
    val expiraEn: String?,
    val ubicacion: String,
    val imagenUrl: String?,
    val esFavorito: Boolean,
    val rating: Float?
)
