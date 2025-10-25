package mx.mfpp.beneficioapp.model

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Gestiona los datos de la sesión del usuario de forma segura utilizando
 * [EncryptedSharedPreferences].
 *
 * Esta clase es responsable de almacenar y recuperar tokens de autenticación
 * (access, refresh), el tipo de usuario (Joven o Negocio) y datos esenciales
 * del perfil (IDs, nombres, etc.) de manera cifrada.
 *
 * @param context El contexto de la aplicación, necesario para inicializar
 * [EncryptedSharedPreferences] y el [MasterKeys].
 */
class SessionManager(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "session_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Guarda los tokens de autenticación y el tipo de usuario en el almacenamiento seguro.
     *
     * @param accessToken El token de acceso (ej. JWT) recibido del servidor.
     * @param refreshToken El token de actualización (opcional) para renovar el token de acceso.
     * @param userType Una cadena que identifica el rol del usuario (ej. "joven", "negocio").
     */
    fun saveToken(accessToken: String, refreshToken: String?, userType: String?) {
        sharedPreferences.edit {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            putString("user_type", userType)
        }
    }

    /**
     * Recupera el token de acceso (Access Token) almacenado.
     *
     * @return El token de acceso como [String], o `null` si no se encuentra.
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    /**
     * Guarda los datos de perfil esenciales para un usuario de tipo "Negocio".
     *
     * @param idNegocio El ID único del negocio.
     * @param nombreNegocio El nombre comercial del negocio.
     */
    fun saveNegocioData(idNegocio: Int, nombreNegocio: String) {
        sharedPreferences.edit {
            putInt("id_negocio", idNegocio)
            putString("nombre_negocio", nombreNegocio)
        }
    }

    /**
     * Guarda los datos de perfil esenciales para un usuario de tipo "Joven".
     *
     * @param idJoven El ID único del usuario (joven).
     * @param nombreJoven El nombre de pila del usuario.
     * @param folioDigital El folio digital de la tarjeta del usuario.
     * @param apellidos Los apellidos del usuario.
     */
    fun saveJovenData(idJoven: Int, nombreJoven: String, folioDigital: String, apellidos: String) {
        sharedPreferences.edit {
            putInt("id_usuario", idJoven)
            putString("nombre", nombreJoven)
            putString("folio_digital", folioDigital)
            putString("apellidos", apellidos)
        }
    }

    /**
     * Guarda o actualiza la URL de la foto de perfil del usuario (joven).
     *
     * @param fotoUrl La URL completa (como String) de la foto. Si es `null`,
     * se guarda una cadena vacía.
     */
    fun setFotoPerfil(fotoUrl: String?) {
        sharedPreferences.edit {
            putString("foto_perfil", fotoUrl ?: "")
        }
    }

    /**
     * Recupera la URL de la foto de perfil del usuario (joven).
     *
     * @return La URL guardada como [String], o una cadena vacía si no está definida.
     */
    fun getFotoPerfil(): String? {
        return sharedPreferences.getString("foto_perfil", "")
    }

    /**
     * Recupera el tipo de usuario (ej. "joven" o "negocio") de la sesión actual.
     *
     * @return El tipo de usuario como [String], o `null` si no se ha guardado.
     */
    fun getUserType(): String? {
        return sharedPreferences.getString("user_type", null)
    }

    /**
     * Recupera el ID del "Negocio" logueado.
     *
     * @return El ID del negocio como [Int], o `null` si no se encuentra (si el valor es -1).
     */
    fun getNegocioId(): Int? {
        val id = sharedPreferences.getInt("id_negocio", -1)
        return if (id != -1) id else null
    }

    /**
     * Recupera el nombre del "Negocio" logueado.
     *
     * @return El nombre del negocio como [String], o `null` si no se encuentra.
     */
    fun getNombreNegocio(): String? {
        return sharedPreferences.getString("nombre_negocio", null)
    }

    /**
     * Recupera el ID del "Joven" (usuario) logueado.
     *
     * @return El ID del usuario como [Int], o `null` si no se encuentra (si el valor es -1).
     */
    fun getJovenId(): Int?{
        val id = sharedPreferences.getInt("id_usuario", -1)
        return if (id != -1) id else null
    }

    /**
     * Recupera el nombre de pila del "Joven" logueado.
     *
     * @return El nombre del usuario como [String], o `null` si no se encuentra.
     */
    fun getNombreJoven(): String? {
        return sharedPreferences.getString("nombre", null)
    }

    /**
     * Recupera los apellidos del "Joven" logueado.
     *
     * @return Los apellidos del usuario como [String], o `null` si no se encuentran.
     */
    fun getApellidosJoven(): String? {
        return sharedPreferences.getString("apellidos", null)
    }

    /**
     * Recupera el folio digital (número de tarjeta) del "Joven" logueado.
     *
     * @return El folio digital como [String], o `null` si no se encuentra.
     */
    fun getFolioTarjeta(): String? {
        return sharedPreferences.getString("folio_digital", null)
    }

    /**
     * Elimina *todos* los datos almacenados en las preferencias seguras.
     * Esto cierra la sesión del usuario de forma efectiva.
     */
    fun clearSession() {
        sharedPreferences.edit {
            clear()
        }
    }

    /**
     * Recupera el token de actualización (Refresh Token) almacenado.
     *
     * @return El token de actualización como [String], o `null` si no se encuentra.
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }
}