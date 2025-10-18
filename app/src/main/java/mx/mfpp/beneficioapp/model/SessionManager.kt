package mx.mfpp.beneficioapp.model

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Clase responsable de la gestión segura de sesión dentro de **BeneficioApp**.
 *
 * Esta clase utiliza [EncryptedSharedPreferences] para almacenar de manera cifrada
 * los tokens de autenticación y el tipo de usuario. Es fundamental para mantener
 * la seguridad de las credenciales y preservar la sesión entre ejecuciones de la app.
 *
 * La clave maestra se genera mediante [MasterKeys] con el estándar de cifrado **AES256-GCM**,
 * garantizando confidencialidad e integridad de los datos almacenados.
 *
 * @constructor Crea una instancia de [SessionManager] que inicializa un almacenamiento
 * seguro de preferencias encriptadas.
 *
 * @param context Contexto de la aplicación necesario para acceder a SharedPreferences.
 */
class SessionManager(context: Context) {
    /** Alias de la clave maestra generada para el cifrado de datos en SharedPreferences. */
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    /**
     * SharedPreferences cifradas que almacenan los tokens y tipo de usuario.
     * Se emplea AES256 tanto para las claves como para los valores.
     */
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "session_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    /**
     * Guarda los tokens de sesión y el tipo de usuario en el almacenamiento cifrado.
     *
     * @param accessToken Token de acceso actual.
     * @param refreshToken Token de actualización (opcional).
     * @param userType Tipo de usuario (por ejemplo: "joven" o "negocio").
     */
    fun saveToken(accessToken: String, refreshToken: String?, userType: String?) {
        sharedPreferences.edit {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            putString("user_type", userType)
        }
    }
    /**
     * Obtiene el token de acceso almacenado.
     *
     * @return Token de acceso o `null` si no existe.
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    /**
     * Obtiene el tipo de usuario actual.
     *
     * @return Tipo de usuario o `null` si no está definido.
     */
    fun getUserType(): String? {
        return sharedPreferences.getString("user_type", null)
    }
    /**
     * Elimina todos los datos de sesión almacenados.
     * Se utiliza al cerrar sesión o limpiar los datos del usuario.
     */
    fun clearSession() {
        sharedPreferences.edit {
            clear()
        }
    }
}