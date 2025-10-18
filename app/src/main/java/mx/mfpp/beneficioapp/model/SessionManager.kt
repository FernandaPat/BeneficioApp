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


    fun getUserType(): String? {
        return sharedPreferences.getString("user_type", null)
    }

    fun clearSession() {
        sharedPreferences.edit {
            clear()
        }
    }
}