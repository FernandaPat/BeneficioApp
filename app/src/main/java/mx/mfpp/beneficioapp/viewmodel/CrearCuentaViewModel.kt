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
/**
 * ViewModel encargado de manejar la creación de una nueva cuenta de usuario.
 *
 * Gestiona los campos del formulario, validación y registro a través del servicio remoto.
 */
class CrearCuentaViewModel : ViewModel() {

    /**
     * Estado que contiene los datos del usuario que se va a registrar.
     */
    var usuario = mutableStateOf(CrearCuentaRequest())

    /**
     * Estado que indica si se está realizando una operación de carga (registro).
     */
    var isLoading = mutableStateOf(false)

    // === HANDLERS PARA CAMPOS ===

    /**
     * Actualiza el nombre del usuario.
     */
    fun onNombreChange(v: String) { usuario.value = usuario.value.copy(nombre = v) }

    /**
     * Actualiza el apellido paterno del usuario.
     */
    fun onApellidoPaternoChange(v: String) { usuario.value = usuario.value.copy(apellidoPaterno = v) }

    /**
     * Actualiza el apellido materno del usuario.
     */
    fun onApellidoMaternoChange(v: String) { usuario.value = usuario.value.copy(apellidoMaterno = v) }

    /**
     * Actualiza la CURP del usuario.
     */
    fun onCurpChange(v: String) { usuario.value = usuario.value.copy(curp = v) }

    /**
     * Actualiza el género del usuario.
     */
    fun onGeneroChange(v: String) { usuario.value = usuario.value.copy(genero = v) }

    /**
     * Actualiza el correo electrónico del usuario.
     */
    fun onCorreoChange(v: String) { usuario.value = usuario.value.copy(correo = v) }

    /**
     * Actualiza el número de teléfono celular del usuario.
     */
    fun onTelefonoChange(v: String) { usuario.value = usuario.value.copy(celular = v) }

    /**
     * Actualiza la contraseña del usuario.
     */
    fun onContrasenaChange(v: String) { usuario.value = usuario.value.copy(password = v) }

    /**
     * Actualiza el folio antiguo del usuario (si aplica).
     */
    fun onFolioChange(v: String) { usuario.value = usuario.value.copy(folio_antiguo = v) }

    /**
     * Indica si el usuario tiene tarjeta, y actualiza el valor correspondiente.
     */
    fun onTieneTarjetaChange(v: Boolean) { usuario.value = usuario.value.copy(tieneTarjeta = v) }

    /**
     * Actualiza la dirección del usuario.
     */
    fun onDireccionChange(v: Direccion) { usuario.value = usuario.value.copy(direccion = v) }

    /**
     * Actualiza el consentimiento del usuario para aceptar términos.
     */
    fun onConsentimientoChange(checked: Boolean) {
        usuario.value = usuario.value.copy(consentimientoAceptado = checked)
    }

    /**
     * Actualiza la fecha de nacimiento del usuario en formato "dd/MM/yyyy".
     */
    fun onFechaChange(d: Int?, m: Int?, a: Int?) {
        if (d != null && m != null && a != null)
            usuario.value = usuario.value.copy(fechaNacimiento = "%02d/%02d/%04d".format(d, m, a))
    }

    // === VALIDACIÓN DEL FORMULARIO ===

    /**
     * Valida si el formulario de registro es correcto.
     *
     * Comprueba que los campos básicos no estén vacíos y que
     * el folio antiguo sea obligatorio si el usuario tiene tarjeta.
     *
     * @return true si el formulario es válido, false en caso contrario.
     */
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

    /**
     * Registra al usuario a través del servicio remoto.
     *
     * @param context Contexto necesario para algunas operaciones de red.
     * @param onResult Lambda que devuelve un Boolean indicando éxito o fallo,
     *                 y un mensaje opcional de error.
     */
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