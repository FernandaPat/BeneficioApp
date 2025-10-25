package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa la respuesta (response) del servidor
 * al intentar crear una nueva cuenta de usuario.
 *
 * @property id_usuario El identificador único del usuario recién creado (puede ser nulo si falla).
 * @property nombre El nombre del usuario registrado (puede ser nulo si falla).
 * @property folio_digital El folio digital asignado al usuario (puede ser nulo si falla).
 * @property mensaje Un mensaje descriptivo sobre el resultado de la operación (ej. "Éxito" o "Error").
 * @property status Un código o texto que indica el estado de la respuesta (ej. "200" o "ERROR").
 */
data class CrearCuentaResponse(
    val id_usuario: String?,
    val nombre: String?,
    val folio_digital: String?,
    val mensaje: String?,
    val status: String?
)