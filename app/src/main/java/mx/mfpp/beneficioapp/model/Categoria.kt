package mx.mfpp.beneficioapp.model

/**
 * Representa una categoría de establecimientos o promociones.
 *
 * Define las diferentes clasificaciones disponibles en la aplicación
 * con su respectivo icono y color identificativo.
 *
 * @property id Identificador único de la categoría
 * @property nombre Nombre legible de la categoría
 * @property iconoResId Resource ID del icono representativo
 * @property color Código de color en formato hexadecimal para la categoría
 */
data class Categoria(
    val id: Int,
    val nombre: String,
    val iconoResId: Int, // Cambiar de String a Int para el resource ID
    val color: String
)