package mx.mfpp.beneficioapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.model.SessionManager

/**
 * ViewModel para manejar el perfil del usuario, incluyendo cierre de sesión.
 *
 * @param application Contexto de la aplicación necesario para SessionManager y Auth0
 */
class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    /** Evento que se emite cuando el usuario cierra sesión */
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    /** Cliente Auth0 para manejar revocación de tokens */
    private val authClient: AuthenticationAPIClient

    init {
        val auth0 = Auth0(
            application.getString(R.string.com_auth0_client_id),
            application.getString(R.string.com_auth0_domain)
        )
        authClient = AuthenticationAPIClient(auth0)
    }

    /**
     * Cierra sesión del usuario.
     *
     * Revoca el refresh token en Auth0 si existe, limpia la sesión local
     * y emite el evento de logout para la UI.
     */
    fun cerrarSesion() {
        viewModelScope.launch {
            val sessionManager = SessionManager(getApplication())

            val refreshToken = sessionManager.getRefreshToken()

            if (refreshToken != null) {
                authClient.revokeToken(refreshToken)
                    .start(object : Callback<Void?, AuthenticationException> {
                        override fun onSuccess(result: Void?) {
                            // Token revocado correctamente
                        }

                        override fun onFailure(error: AuthenticationException) {
                            Log.w("AUTH0_LOGOUT", "No se pudo revocar el refresh token: ${error.getDescription()}")
                        }
                    })
            }

            sessionManager.clearSession()
            _logoutEvent.emit(Unit)
        }
    }
}