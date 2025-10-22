package mx.mfpp.beneficioapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.model.ServicioRemotoRecuperarContrasena
import mx.mfpp.beneficioapp.model.SessionManager

sealed class RecuperarState {
    object Idle : RecuperarState()
    object Loading : RecuperarState()
    object Success : RecuperarState()
    data class Error(val message: String) : RecuperarState()
}

class RecuperarContrasenaViewModel(application: Application) : AndroidViewModel(application) {

    var email = mutableStateOf("")
        private set

    private val _recuperarState = MutableStateFlow<RecuperarState>(RecuperarState.Idle)
    val recuperarState: StateFlow<RecuperarState> = _recuperarState

    private val auth0 = Auth0(
        application.getString(R.string.com_auth0_client_id),
        application.getString(R.string.com_auth0_domain)
    )
    private val authClient = AuthenticationAPIClient(auth0)

    // SessionManager para obtener ID del joven
    private val sessionManager = SessionManager(application)

    init {
        // Cargar email del usuario logueado si existe
        cargarEmailDeUsuarioLogueado()
    }

    /**
     * Carga el email del usuario logueado desde el servidor.
     * Utiliza ServicioRemotoRecuperarContrasena para obtener los datos.
     */
    private fun cargarEmailDeUsuarioLogueado() {
        val tipoUsuario = sessionManager.getUserType()

        // --- CORRECCIÓN DE SCOPE ---
        // Definimos los IDs aquí arriba para que existan en toda la función
        val idUsuario = sessionManager.getJovenId() ?: 0
        val idEstablecimiento = sessionManager.getNegocioId() ?: 0
        // --- FIN CORRECCIÓN DE SCOPE ---

        // Estos logs ahora funcionan porque las variables existen
        if (tipoUsuario == "establecimiento") {
            Log.d("RECUPERAR_PASSWORD", "🔍 Buscando email para establecimiento ID: $idEstablecimiento")
        } else if (tipoUsuario == "joven") {
            Log.d("RECUPERAR_PASSWORD", "🔍 Buscando email para joven ID: $idUsuario")
        } else {
            Log.d("RECUPERAR_PASSWORD", "🔍 No hay usuario logueado o tipo desconocido ($tipoUsuario)")
        }

        // Llamar al servicio remoto en background
        viewModelScope.launch {
            try {
                // --- CORRECCIÓN DE LÓGICA ---
                // Decidimos qué servicio llamar basándonos en el tipo de usuario
                val response = if (tipoUsuario == "establecimiento") {
                    ServicioRemotoRecuperarContrasena.obtenerEmailEstablecimiento(idEstablecimiento)
                } else if (tipoUsuario == "joven") {
                    ServicioRemotoRecuperarContrasena.obtenerEmailJoven(idUsuario)
                } else {
                    // Si no hay tipo de usuario, no podemos llamar a ningún servicio
                    null
                }
                // --- FIN CORRECCIÓN DE LÓGICA ---

                if (response != null) {
                    email.value = response.email
                    Log.d("RECUPERAR_PASSWORD", "✅ Email cargado: ${response.email} (${response.tipoUsuario})")
                } else {
                    Log.d("RECUPERAR_PASSWORD", "⚠️ No se pudo obtener el email del servidor (tipo: $tipoUsuario)")
                }
            } catch (e: Exception) {
                Log.e("RECUPERAR_PASSWORD", "❌ Error al cargar email: ${e.message}")
                // Si falla, el campo permanece vacío para que el usuario lo escriba
            }
        }
    }

    fun onEmailChange(value: String) {
        email.value = value
    }

    fun esEmailValido(): Boolean {
        return email.value.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
    }

    fun solicitarRecuperacion() {
        if (!esEmailValido()) {
            _recuperarState.value = RecuperarState.Error("Por favor, ingresa un correo válido")
            return
        }

        Log.d("RECUPERAR_PASSWORD", "📧 Solicitando recuperación para: ${email.value}")
        _recuperarState.value = RecuperarState.Loading

        authClient
            .resetPassword(email.value, "BeneficioJovenMovil") // ← VERIFICA ESTE NOMBRE
            .start(object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    Log.d("RECUPERAR_PASSWORD", "✅ Email enviado exitosamente")
                    _recuperarState.value = RecuperarState.Success
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("RECUPERAR_PASSWORD", "❌ Error: ${error.getDescription()}")
                    Log.e("RECUPERAR_PASSWORD", "Code: ${error.getCode()}")

                    val mensaje = when {
                        error.getDescription()?.contains("user does not exist", ignoreCase = true) == true ->
                            "No existe una cuenta con este correo"
                        error.getDescription()?.contains("rate limit", ignoreCase = true) == true ->
                            "Has intentado demasiadas veces. Espera unos minutos"
                        error.getDescription()?.contains("connection", ignoreCase = true) == true ->
                            "Error de conexión. Verifica el nombre de la conexión"
                        else ->
                            "Error al enviar el correo. Verifica tu conexión e intenta de nuevo"
                    }

                    _recuperarState.value = RecuperarState.Error(mensaje)
                }
            })
    }

    fun resetState() {
        _recuperarState.value = RecuperarState.Idle
    }
}