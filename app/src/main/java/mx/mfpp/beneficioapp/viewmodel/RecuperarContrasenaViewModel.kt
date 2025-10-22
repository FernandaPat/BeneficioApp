package mx.mfpp.beneficioapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mx.mfpp.beneficioapp.R

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

        Log.d("RECUPERAR_PASSWORD", "Solicitando recuperación para: ${email.value}")
        _recuperarState.value = RecuperarState.Loading

        authClient
            .resetPassword(email.value, "BeneficioJovenMovil")
            .start(object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    Log.d("RECUPERAR_PASSWORD", "✅ Email enviado exitosamente")
                    _recuperarState.value = RecuperarState.Success
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("RECUPERAR_PASSWORD", "❌ Error: ${error.getDescription()}")

                    val mensaje = when {
                        error.getDescription()?.contains("user does not exist", ignoreCase = true) == true ->
                            "No existe una cuenta con este correo"
                        error.getDescription()?.contains("rate limit", ignoreCase = true) == true ->
                            "Has intentado demasiadas veces. Espera unos minutos"
                        else ->
                            "Error al enviar el correo. Verifica tu conexión e intenta de nuevo"
                    }

                    _recuperarState.value = RecuperarState.Error(mensaje)
                }
            })
    }

    fun resetState() {
        _recuperarState.value = RecuperarState.Idle
        email.value = ""
    }
}