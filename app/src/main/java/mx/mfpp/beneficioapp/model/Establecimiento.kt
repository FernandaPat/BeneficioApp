package mx.mfpp.beneficioapp.model

data class Establecimiento(
    val id: String,
    val nombre: String,
    val categoria: String,
    val tiempo: String,
    val distancia: String,
    val imagenUrl: String? = null,
    val rating: Double = 0.0,
    val isFavorito: Boolean = false
)