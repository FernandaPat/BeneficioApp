package mx.mfpp.beneficioapp.model

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SessionManager(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "session_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

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
    fun saveNegocioData(idNegocio: Int, nombreNegocio: String) {
        sharedPreferences.edit {
            putInt("id_negocio", idNegocio)
            putString("nombre_negocio", nombreNegocio)
        }
    }

    fun saveJovenData(idJoven: Int, nombreJoven: String, folioDigital: String, apellidos: String) {
        sharedPreferences.edit {
            putInt("id_usuario", idJoven)
            putString("nombre", nombreJoven)
            putString("folio_digital", folioDigital)
            putString("apellidos", apellidos)
        }
    }
    // 🔹 Guarda la URL de la foto del perfil del joven
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