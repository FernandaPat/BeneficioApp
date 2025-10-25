package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa el perfil de un negocio o establecimiento.
 *
 * @property id El identificador único del negocio.
 * @property nombre El nombre comercial del negocio.
 * @property correo La dirección de correo electrónico de contacto del negocio.
 * @property telefono El número de teléfono de contacto del negocio.
 * @property direccion La dirección postal del negocio (probablemente formateada como String).
 * @property categoria La categoría a la que pertenece el negocio (ej. "Restaurante", "Ropa").
 * @property foto La URL de la foto de perfil o logo del negocio (puede ser nula).
 */
data class Negocio(
    val id: Int,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val categoria: String,
    val foto: String?
)