package mx.mfpp.beneficioapp.model

data class Categoria(
    val id: Int,
    val nombre: String,
    val iconoResId: Int, // Cambiar de String a Int para el resource ID
    val color: String
)