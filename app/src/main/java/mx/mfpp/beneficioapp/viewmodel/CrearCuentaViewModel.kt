package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.CrearCuentaRequest
import mx.mfpp.beneficioapp.model.Direccion
import mx.mfpp.beneficioapp.model.ServicioRemotoCrearCuenta
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.utils.ErrorHandler

class CrearCuentaViewModel : ViewModel() {

    var usuario = mutableStateOf(CrearCuentaRequest())
    var isLoading = mutableStateOf(false)

    // === HANDLERS PARA CAMPOS ===
    fun onNombreChange(v: String) { usuario.value = usuario.value.copy(nombre = v) }
    fun onApellidoPaternoChange(v: String) { usuario.value = usuario.value.copy(apellidoPaterno = v) }
    fun onApellidoMaternoChange(v: String) { usuario.value = usuario.value.copy(apellidoMaterno = v) }
    fun onCurpChange(v: String) { usuario.value = usuario.value.copy(curp = v) }
    fun onGeneroChange(v: String) { usuario.value = usuario.value.copy(genero = v) }
    fun onCorreoChange(v: String) { usuario.value = usuario.value.copy(correo = v) }
    fun onTelefonoChange(v: String) { usuario.value = usuario.value.copy(celular = v) }
    fun onContrasenaChange(v: String) { usuario.value = usuario.value.copy(password = v) }
    fun onFolioChange(v: String) { usuario.value = usuario.value.copy(folio_antiguo = v) }
    fun onTieneTarjetaChange(v: Boolean) { usuario.value = usuario.value.copy(tieneTarjeta = v) }
    fun onDireccionChange(v: Direccion) { usuario.value = usuario.value.copy(direccion = v) }
    fun onConsentimientoChange(v: Boolean) { usuario.value = usuario.value.copy(consentimientoAceptado = v) }

    fun onFechaChange(d: Int?, m: Int?, a: Int?) {
        if (d != null && m != null && a != null)
            usuario.value = usuario.value.copy(fechaNacimiento = "%02d/%02d/%04d".format(d, m, a))
    }

    // === VALIDACIÓN DEL FORMULARIO ===
    fun esFormularioValido(): Boolean {
        val usuario = usuario.value
        val camposBasicos = usuario.nombre.isNotBlank() &&
                usuario.apellidoPaterno.isNotBlank() &&
                usuario.apellidoMaterno.isNotBlank() &&
                usuario.curp.isNotBlank() &&
                usuario.correo.isNotBlank() &&
                usuario.password.isNotBlank() &&
                usuario.consentimientoAceptado

        val folioValido = if (usuario.tieneTarjeta == true) {
            !usuario.folio_antiguo.isNullOrBlank()
        } else true

        return camposBasicos && folioValido
    }

    // === REGISTRO DE USUARIO ===
    fun registrarUsuario(
        context: Context,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("CrearCuenta", "Registrando usuario...")
                val response = ServicioRemotoCrearCuenta.api.registrarUsuario(usuario.value)

                if (response.isSuccessful) {
                    val usuarioRegistrado = response.body()
                    Log.d("CrearCuenta", "Registro exitoso: $usuarioRegistrado")


                    onResult(true, null)
                } else {
                    val errorJson = response.errorBody()?.string()
                    val mensajeError = try {
                        val json = org.json.JSONObject(errorJson ?: "")
                        json.optString("message", "Error desconocido al registrar usuario")
                    } catch (e: Exception) {
                        errorJson ?: "Error desconocido al registrar usuario"
                    }
                    onResult(false, mensajeError)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                val mensaje = ErrorHandler.obtenerMensajeError(e)
                onResult(false, mensaje)
            }
        }
    }
}
