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
/**
 * Representa los distintos estados del login de un negocio.
 */
sealed class LoginStateNegocio {
    /** Estado inicial, sin actividad de login */
    object Idle : LoginStateNegocio()

    /** Estado de carga durante el login */
    object Loading : LoginStateNegocio()

    /** Estado exitoso de login */
    data class Success(val accessToken: String) : LoginStateNegocio()

    /** Estado de error en el login */
    data class Error(val message: String) : LoginStateNegocio()
}

/**
 * ViewModel para manejar el inicio de sesión de negocios.
 *
 * Permite actualizar campos de formulario, validar datos, iniciar sesión
 * con Auth0, y guardar la sesión y datos del negocio.
 *
 * @param application Contexto de la aplicación necesario para obtener recursos y SharedPreferences.
 */
class IniciarSesionNegocioViewModel(application: Application) : AndroidViewModel(application) {

    /** Datos del formulario de login */
    var login = mutableStateOf(LoginNegocioRequest())
        private set

    /** Estado actual del login */
    private val _loginState = MutableStateFlow<LoginStateNegocio>(LoginStateNegocio.Idle)
    val loginState: StateFlow<LoginStateNegocio> = _loginState

    /** Instancia de Auth0 */
    private val auth0 = Auth0(
        application.getString(R.string.com_auth0_client_id),
        application.getString(R.string.com_auth0_domain)
    )

    /** Cliente de autenticación de Auth0 */
    private val authClient = AuthenticationAPIClient(auth0)

    init {
        Log.d("AUTH0_INIT", "Domain: ${application.getString(R.string.com_auth0_domain)}")
        Log.d("AUTH0_INIT", "Client ID: ${application.getString(R.string.com_auth0_client_id).take(10)}...")
    }

    /**
     * Actualiza el correo del formulario de login.
     *
     * @param value Correo electrónico ingresado.
     */
    fun onCorreoChange(value: String) {
        login.value = login.value.copy(correo = value)
    }

    /**
     * Actualiza la contraseña del formulario de login.
     *
     * @param value Contraseña ingresada.
     */
    fun onPasswordChange(value: String) {
        login.value = login.value.copy(password = value)
    }

    /**
     * Valida que los campos del formulario estén completos.
     *
     * @return true si correo y contraseña no están vacíos.
     */
    fun esFormularioValido(): Boolean {
        val correo = login.value.correo
        val password = login.value.password
        return correo.isNotBlank() && password.isNotBlank()
    }

    /**
     * Inicia sesión con Auth0 usando los datos del formulario.
     *
     * Maneja estados de carga, éxito y error, y guarda los tokens
     * y datos del negocio en SessionManager.
     */
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

                    val idNegocio = jwt.getClaim(namespace + "id_negocio").asInt() ?: -1
                    val nombreNegocio = jwt.getClaim(namespace + "nombre_negocio").asString() ?: "Negocio"

                    val sessionManager = SessionManager(getApplication())
                    sessionManager.saveToken(accessToken, refreshToken, userType)
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

    /**
     * Guarda un token de acceso de forma segura en SharedPreferences.
     *
     * @param token Token de acceso a guardar.
     */
    private fun saveTokenSecurely(token: String) {
        val prefs = getApplication<Application>().getSharedPreferences("auth", Application.MODE_PRIVATE)
        prefs.edit {
            putString("access_token", token)
        }
        Log.d("AUTH0_TOKEN", "Token guardado")
    }

    /**
     * Reinicia el estado de login al estado inicial (Idle).
     */
    fun resetState() {
        _loginState.value = LoginStateNegocio.Idle
    }
}