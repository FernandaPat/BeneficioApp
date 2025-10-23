/**
 * Archivo: Joven.kt
 *
 * Define el modelo de datos que representa la información básica de un usuario joven.
 *
 * Esta clase se utiliza para mostrar o manipular los datos personales del usuario
 * dentro de la aplicación, así como para recibirlos desde el servidor.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa a un usuario joven registrado en el sistema.
 *
 * Contiene los datos principales asociados al perfil del usuario.
 *
 * @property id Identificador único del usuario
 * @property nombre Nombre completo del joven
 * @property correo Correo electrónico del usuario
 * @property telefono Número de teléfono del usuario
 * @property direccion Dirección completa del usuario
 * @property foto URL de la foto de perfil (opcional)
 */
data class Joven(
    val id: Int,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val foto: String?

)
