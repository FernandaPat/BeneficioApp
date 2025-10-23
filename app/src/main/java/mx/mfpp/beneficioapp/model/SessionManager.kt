/**
 * Archivo: SessionManager.kt
 *
 * Gestiona de forma segura la información de sesión del usuario dentro de la aplicación.
 *
 * Utiliza `EncryptedSharedPreferences` para almacenar de forma cifrada datos sensibles como:
 * - Tokens de autenticación
 * - Información de perfil (nombre, foto, folio)
 * - Datos del negocio o del joven autenticado
 *
 * Esta clase es responsable de persistir y recuperar la información de sesión
 * para mantener la experiencia de usuario sin requerir nuevos inicios de sesión frecuentes.
 */
package mx.mfpp.beneficioapp.model

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
/**
 * Clase encargada de manejar la persistencia segura de los datos de sesión del usuario.
 *
 * Se utiliza tanto para usuarios jóvenes como para negocios, de acuerdo con el tipo de sesión iniciada.
 * Todos los datos se almacenan cifrados mediante las APIs de seguridad de Android.
 *
 * @param context Contexto de la aplicación necesario para acceder a `SharedPreferences`.
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
     * Guarda los tokens de sesión y el tipo de usuario.
     *
     * @param accessToken Token de acceso JWT o equivalente.
     * @param refreshToken Token de actualización (puede ser nulo).
     * @param userType Tipo de usuario: "joven" o "negocio".
     */
    fun saveToken(accessToken: String, refreshToken: String?, userType: String?) {
        sharedPreferences.edit {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            putString("user_type", userType)
        }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }
    /**
     * Guarda los datos principales del negocio autenticado.
     *
     * @param idNegocio ID del negocio.
     * @param nombreNegocio Nombre del establecimiento.
     */
    fun saveNegocioData(idNegocio: Int, nombreNegocio: String) {
        sharedPreferences.edit {
            putInt("id_negocio", idNegocio)
            putString("nombre_negocio", nombreNegocio)
        }
    }
    /**
     * Guarda los datos de perfil del joven autenticado.
     *
     * @param idJoven ID del usuario joven.
     * @param nombreJoven Nombre de pila del joven.
     * @param folioDigital Folio digital asignado al joven.
     * @param apellidos Apellidos completos del joven.
     */
    fun saveJovenData(idJoven: Int, nombreJoven: String, folioDigital: String, apellidos: String) {
        sharedPreferences.edit {
            putInt("id_usuario", idJoven)
            putString("nombre", nombreJoven)
            putString("folio_digital", folioDigital)
            putString("apellidos", apellidos)
        }
    }


    //Guarda la URL de la foto del perfil del joven
    fun setFotoPerfil(fotoUrl: String?) {
        sharedPreferences.edit {
            putString("foto_perfil", fotoUrl ?: "")
        }
    }


    // 🔹 Recupera la URL de la foto del perfil del joven
    fun getFotoPerfil(): String? {
        return sharedPreferences.getString("foto_perfil", "")
    }


    fun getUserType(): String? {
        return sharedPreferences.getString("user_type", null)
    }

    // 🔹 Recupera el ID del negocio
    fun getNegocioId(): Int? {
        val id = sharedPreferences.getInt("id_negocio", -1)
        return if (id != -1) id else null
    }

    // 🔹 Recupera el nombre del negocio
    fun getNombreNegocio(): String? {
        return sharedPreferences.getString("nombre_negocio", null)
    }

    fun getJovenId(): Int?{
        val id = sharedPreferences.getInt("id_usuario", -1)
        return if (id != -1) id else null
    }

    fun getNombreJoven(): String? {
        return sharedPreferences.getString("nombre", null)
    }

    fun getApellidosJoven(): String? {
        return sharedPreferences.getString("apellidos", null)
    }

    fun getFolioTarjeta(): String? {
        return sharedPreferences.getString("folio_digital", null)
    }


    // 🔹 Limpia toda la sesión
    fun clearSession() {
        sharedPreferences.edit {
            clear()
        }
    }
    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }
}