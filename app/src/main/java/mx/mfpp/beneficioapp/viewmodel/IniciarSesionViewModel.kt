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
import mx.mfpp.beneficioapp.model.LoginRequest
import androidx.core.content.edit
import mx.mfpp.beneficioapp.model.SessionManager
import com.auth0.android.jwt.JWT
import mx.mfpp.beneficioapp.utils.AuthErrorUtils
import mx.mfpp.beneficioapp.utils.FcmHelper

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val accessToken: String, val userId: Int) : LoginState() // ✅ AGREGAR userId
    data class Error(val message: String) : LoginState()
}

class IniciarSesionViewModel(application: Application) : AndroidViewModel(application) {

    var login = mutableStateOf(LoginRequest())
        private set

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

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

        return when {
            correo.isBlank() -> false
            password.isBlank() -> false
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> false
            else -> true
        }
    }

    fun iniciarSesion() {

        if (!esFormularioValido()) {
            Log.d("AUTH0_LOGIN", "Formulario no válido")
            _loginState.value = LoginState.Error("Correo y contraseña válidos requeridos")
            return
        }

        Log.d("AUTH0_LOGIN", "Formulario válido")
        Log.d("AUTH0_LOGIN", "Email: ${login.value.correo}")
        Log.d("AUTH0_LOGIN", "Iniciando autenticación con Auth0...")

        _loginState.value = LoginState.Loading

        authClient
            .login(login.value.correo, login.value.password)
            .setScope("openid profile email offline_access")
            .validateClaims()
            .start(object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {

                    val idToken = result.idToken
                    val accessToken = result.accessToken
                    val refreshToken = result.refreshToken

                    // Decodificar JWT para obtener datos del usuario
                    val jwt = JWT(idToken)
                    val namespace = "https://api.beneficiojoven.com/"
                    val userType = jwt.getClaim(namespace + "tipo_usuario").asString()
                    val idJoven = jwt.getClaim(namespace + "id_usuario").asInt() ?: -1
                    val nombreJoven = jwt.getClaim(namespace + "nombre_completo").asString() ?: "Joven"
                    val folioDigital = jwt.getClaim(namespace + "folio_digital").asString() ?: "0"

                    Log.d("AUTH0_SUCCESS", "Tipo de usuario: $userType")
                    Log.d("AUTH0_SUCCESS", "ID joven: $idJoven")
                    Log.d("AUTH0_SUCCESS", "Nombre: $nombreJoven")
                    Log.d("AUTH0_SUCCESS", "Folio: $folioDigital")

                    // Guardar sesión
                    val sessionManager = SessionManager(getApplication())
                    sessionManager.saveToken(accessToken, refreshToken, userType)
                    sessionManager.saveJovenData(idJoven, nombreJoven, folioDigital)

                    Log.d("AUTH0_SUCCESS", "Sesión guardada en SessionManager")

                    // ✅ REGISTRAR TOKEN FCM (SOLO PARA JÓVENES)
                    if (userType == "joven" && idJoven != -1) {
                        Log.d("AUTH0_SUCCESS", "Usuario es JOVEN → Registrando token FCM")
                        FcmHelper.registrarTokenEnServidor(idJoven)
                    } else {
                        Log.d("AUTH0_SUCCESS", "⚠Usuario NO es joven o ID inválido → No se registra token FCM")
                    }

                    _loginState.value = LoginState.Success(accessToken, idJoven)
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("AUTH0_ERROR", "ERROR EN LOGIN")
                    Log.e("AUTH0_ERROR", "Código: ${error.getCode()}")
                    Log.e("AUTH0_ERROR", "Descripción: ${error.getDescription()}")

                    val mensaje = AuthErrorUtils.obtenerMensaje(error)
                    _loginState.value = LoginState.Error(mensaje)
                }
            })
    }

    private fun saveTokenSecurely(token: String) {
        val prefs = getApplication<Application>().getSharedPreferences("auth", Application.MODE_PRIVATE)
        prefs.edit {
            putString("access_token", token)
        }
        Log.d("AUTH0_TOKEN", "Token guardado")
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}