package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una promoción o oferta disponible en la aplicación.
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



