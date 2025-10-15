package mx.mfpp.beneficioapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import mx.mfpp.beneficioapp.model.LoginRequest

class IniciarSesionViewModel : ViewModel() {

    var login = mutableStateOf(LoginRequest())

    // === HANDLERS ===
    fun onCorreoChange(value: String) {
        login.value = login.value.copy(correo = value)
    }

    fun onPasswordChange(value: String) {
        login.value = login.value.copy(password = value)
    }

    // === VALIDACIÓN ===
    fun esFormularioValido(): Boolean {
        val correo = login.value.correo
        val password = login.value.password
        return (correo.isNotBlank() && password.isNotBlank())
    }

    // === CONSTRUCCIÓN DEL REQUEST ===
    fun construirRequest(): LoginRequest = login.value
}
