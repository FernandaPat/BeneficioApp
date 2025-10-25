package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa el perfil de un usuario (joven).
 *
 * @property id El identificador único del joven.
 * @property nombre El nombre completo del joven.
 * @property correo La dirección de correo electrónico del joven.
 * @property telefono El número de teléfono del joven.
 * @property direccion La dirección postal (probablemente formateada como String).
 * @property foto La URL de la foto de perfil del joven (puede ser nula).
 */
data class Joven(
    val id: Int,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val foto: String?
)