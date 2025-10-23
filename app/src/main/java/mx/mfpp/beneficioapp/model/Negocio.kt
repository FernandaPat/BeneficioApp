/**
 * Archivo: Negocio.kt
 *
 * Define el modelo de datos que representa la información básica de un negocio registrado.
 *
 * Esta clase se utiliza para mostrar los datos del establecimiento en la interfaz
 * o para mapear la respuesta JSON obtenida desde el servidor.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa un negocio o establecimiento dentro del sistema.
 *
 * Contiene los datos principales asociados al perfil del negocio.
 *
 * @property id Identificador único del negocio
 * @property nombre Nombre comercial del negocio
 * @property correo Correo electrónico del negocio
 * @property telefono Número de contacto del negocio
 * @property direccion Dirección completa del establecimiento
 * @property categoria Categoría o tipo de negocio (por ejemplo, "Restaurante", "Belleza")
 * @property foto URL de la foto de perfil del negocio (opcional)
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
