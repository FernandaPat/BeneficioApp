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

/**
 * Representa los posibles estados del proceso de inicio de sesión de un negocio.
 */
sealed class LoginStateNegocio {
    object Idle : LoginStateNegocio()
    object Loading : LoginStateNegocio()
    data class Success(val accessToken: String) : LoginStateNegocio()
    data class Error(val message: String) : LoginStateNegocio()
}
/**
 * ViewModel responsable del proceso de autenticación de negocios en **BeneficioApp**.
 *
 * Utiliza el SDK de **Auth0** para validar credenciales, obtener tokens y guardar
 * la sesión de manera segura mediante [SessionManager].
 *
 * También gestiona el estado de la interfaz (correo, contraseña, errores, carga, éxito)
 * y la persistencia del token JWT.
 *
 * @param application Contexto global necesario para acceder a recursos y preferencias.
 */
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
    /**
     * Actualiza el valor del campo de correo en el formulario.
     *
     * @param value Nuevo correo ingresado por el usuario.
     */
    fun onCorreoChange(value: String) {
        login.value = login.value.copy(correo = value)
    }

    /**
     * Actualiza el valor del campo de contraseña en el formulario.
     *
     * @param value Nueva contraseña ingresada por el usuario.
     */
    fun onPasswordChange(value: String) {
        login.value = login.value.copy(password = value)
    }
    /**
     * Verifica si el formulario de inicio de sesión contiene datos válidos.
     *
     * @return `true` si ambos campos (correo y contraseña) no están vacíos.
     */

    fun esFormularioValido(): Boolean {
        val correo = login.value.correo
        val password = login.value.password
        return correo.isNotBlank() && password.isNotBlank()
    }
    /**
     * Inicia el proceso de autenticación con Auth0.
     *
     * Valida primero que el formulario esté completo, luego solicita autenticación
     * al servidor Auth0 con las credenciales del negocio. Si la autenticación es exitosa:
     * - Obtiene el token JWT.
     * - Extrae el tipo de usuario desde el *namespace* personalizado.
     * - Guarda los tokens de sesión de forma segura con [SessionManager].
     *
     * En caso de error, actualiza el estado con un mensaje descriptivo.
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

                    Log.d("AUTH0_SUCCESS", "Tipo de usuario: $userType")

                    val sessionManager= SessionManager(getApplication())
                    sessionManager.saveToken(accessToken, refreshToken, userType)

                    _loginState.value = LoginStateNegocio.Success(accessToken)

                    Log.d("AUTH0_SUCCESS", "Login exitoso!")
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("AUTH0_ERROR", "${error.getCode()}: ${error.getDescription()}")

                    val errorMessage = when (error.getCode()) {
                        "invalid_grant", "invalid_user_password" -> "Email o contraseña incorrectos"
                        "too_many_attempts" -> "Demasiados intentos"
                        "unauthorized" -> "Acceso no autorizado"
                        else -> error.getDescription()
                    }

                    _loginState.value = LoginStateNegocio.Error(errorMessage)
                }
            })
    }
    /**
     * Guarda el token de acceso en **SharedPreferences** de manera segura.
     * Este método se utiliza como respaldo adicional de [SessionManager].
     *
     * @param token Token JWT obtenido del proceso de autenticación.
     */
    private fun saveTokenSecurely(token: String) {
        val prefs = getApplication<Application>().getSharedPreferences("auth", Application.MODE_PRIVATE)
        prefs.edit { // The KTX function provides a safe block
            putString("access_token", token)
        }
        Log.d("AUTH0_TOKEN", "Token guardado")
    }
    /**
     * Restablece el estado del login a [LoginStateNegocio.Idle].
     * Se llama al finalizar cada intento de inicio de sesión.
     */
    fun resetState() {
        _loginState.value = LoginStateNegocio.Idle
    }
}
