package mx.mfpp.beneficioapp.model

data class LoginRequest(
    val correo: String = "",
    val password: String = ""
)
