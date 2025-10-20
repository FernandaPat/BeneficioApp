package mx.mfpp.beneficioapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.result.Credentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mx.mfpp.beneficioapp.R
import androidx.core.content.edit
import mx.mfpp.beneficioapp.model.LoginNegocioRequest
import mx.mfpp.beneficioapp.model.SessionManager
import com.auth0.android.jwt.JWT
import mx.mfpp.beneficioapp.utils.AuthErrorUtils


sealed class LoginStateNegocio {
    object Idle : LoginStateNegocio()
    object Loading : LoginStateNegocio()
    data class Success(val accessToken: String) : LoginStateNegocio()
    data class Error(val message: String) : LoginStateNegocio()
}
class IniciarSesionNegocioViewModel(application: Application) : AndroidViewModel(application) {

    var login = mutableStateOf(LoginNegocioRequest())
        private set


    private val _loginState = MutableStateFlow<LoginStateNegocio>(LoginStateNegocio.Idle)
    val loginState: StateFlow<LoginStateNegocio> = _loginState

    private val auth0 = Auth0(
        application.getString(R.string.com_auth0_client_id),
        application.getString(R.string.com_auth0_domain)
    )

    private val authClient = AuthenticationAPIClient(auth0)

    init {
        Log.d("AUTH0_INIT", "Domain: ${application.getString(R.string.com_auth0_domain)}")
        Log.d("AUTH0_INIT", "Client ID: ${application.getString(R.string.com_auth0_client_id).take(10)}...")
    }

    fun onCorreoChange(value: String) {
        login.value = login.value.copy(correo = value)
    }

    fun onPasswordChange(value: String) {
        login.value = login.value.copy(password = value)
    }

    fun esFormularioValido(): Boolean {
        val correo = login.value.correo
        val password = login.value.password
        return correo.isNotBlank() && password.isNotBlank()
    }

    fun iniciarSesion() {
        Log.d("AUTH0_LOGIN", "iniciarSesion() llamado")

        if (!esFormularioValido()) {
            Log.d("AUTH0_LOGIN", "Formulario no válido")
            _loginState.value = LoginStateNegocio.Error("Correo y contraseña válidos requeridos")
            return
        }

        Log.d("AUTH0_LOGIN", "Iniciando login con Auth0")
        Log.d("AUTH0_LOGIN", "Email: ${login.value.correo}")

        _loginState.value = LoginStateNegocio.Loading

        authClient
            .login(login.value.correo, login.value.password)
            .setScope("openid profile email offline_access")
            .validateClaims()
            .start(object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    val idToken = result.idToken
                    val accessToken = result.accessToken
                    val refreshToken = result.refreshToken

                    val jwt = JWT(idToken)
                    val namespace = "https://api.beneficiojoven.com/"
                    val userType = jwt.getClaim(namespace + "tipo_usuario").asString()

                    // 🔹 Extrae los datos del negocio (claims personalizados)
                    val idNegocio = jwt.getClaim(namespace + "id_negocio").asInt() ?: -1
                    val nombreNegocio = jwt.getClaim(namespace + "nombre_negocio").asString() ?: "Negocio"

                    val sessionManager = SessionManager(getApplication())
                    sessionManager.saveToken(accessToken, refreshToken, userType)

                    // 🔹 Aquí guardas el nombre y el ID del negocio encriptados también
                    sessionManager.saveNegocioData(idNegocio, nombreNegocio)

                    Log.d("AUTH0_SUCCESS", "Tipo de usuario: $userType")
                    Log.d("AUTH0_SUCCESS", "ID negocio: $idNegocio")
                    Log.d("AUTH0_SUCCESS", "Nombre negocio: $nombreNegocio")


                    _loginState.value = LoginStateNegocio.Success(accessToken)

                    Log.d("AUTH0_SUCCESS", "Login exitoso!")
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("AUTH0_ERROR", "${error.getCode()}: ${error.getDescription()}")
                    val mensaje = AuthErrorUtils.obtenerMensaje(error)
                    _loginState.value = LoginStateNegocio.Error(mensaje)
                }

            })
    }

    private fun saveTokenSecurely(token: String) {
        val prefs = getApplication<Application>().getSharedPreferences("auth", Application.MODE_PRIVATE)
        prefs.edit { // The KTX function provides a safe block
            putString("access_token", token)
        }
        Log.d("AUTH0_TOKEN", "Token guardado")
    }

    fun resetState() {
        _loginState.value = LoginStateNegocio.Idle
    }
}
