package mx.mfpp.beneficioapp.model

/**
 * Modelo de datos que representa la respuesta (response) del servidor
 * al intentar registrar un nuevo usuario (joven).
 *
 * @property id_usuario El identificador único del usuario recién creado (puede ser nulo si falla).
 * @property mensaje Un mensaje descriptivo sobre el resultado de la operación (ej. "Registro exitoso").
 * @property status Un código o texto que indica el estado de la respuesta (ej. "201" o "ERROR").
 */
data class RegistroJovenResponse(
    val id_usuario: Int?,
    val mensaje: String?,
    val status: String?
)