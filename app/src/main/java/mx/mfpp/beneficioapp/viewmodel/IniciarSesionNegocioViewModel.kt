package mx.mfpp.beneficioapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import mx.mfpp.beneficioapp.model.LoginNegocioRequest

class IniciarSesionNegocioViewModel : ViewModel() {

    var login = mutableStateOf(LoginNegocioRequest())

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

    fun construirRequest(): LoginNegocioRequest = login.value
}
