package mx.mfpp.beneficioapp.model

/**
 * Representa un establecimiento comercial en la aplicación.
 *
 * Contiene información básica del negocio como ubicación, categoría
 * y detalles de contacto o valoración.
 *
 * @property id Identificador único del establecimiento
 * @property nombre Nombre comercial del establecimiento
 * @property categoria Categoría principal del establecimiento
 * @property tiempo Tiempo estimado de llegada al establecimiento
 * @property distancia Distancia aproximada al establecimiento
 * @property imagenUrl URL opcional de la imagen del establecimiento
 * @property rating Valoración numérica del establecimiento
 * @property isFavorito Indica si el establecimiento está marcado como favorito
 */
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