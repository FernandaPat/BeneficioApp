package mx.mfpp.beneficioapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.AgregarPromocionRequest
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.model.ServicioRemotoAgregarPromocion
import mx.mfpp.beneficioapp.utils.ErrorHandler
import mx.mfpp.beneficioapp.utils.ImageUtils
/**
 * ViewModel encargado de manejar la lógica de agregar una promoción.
 *
 * @property uri Estado que contiene la URI de la imagen de la promoción.
 * @property nombre Estado que contiene el nombre o título de la promoción.
 * @property descripcion Estado que contiene la descripción de la promoción.
 * @property descuento Estado que contiene el valor del descuento de la promoción.
 * @property desde Estado que indica la fecha de inicio de la promoción.
 * @property hasta Estado que indica la fecha de fin de la promoción.
 * @property isLoading Estado que indica si se está procesando la solicitud de agregar la promoción.
 */
class AgregarPromocionViewModel(application: Application) : AndroidViewModel(application) {

    // === CAMPOS DEL FORMULARIO ===
    val uri = mutableStateOf<Uri?>(null)
    val nombre = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val descuento = mutableStateOf("")
    val desde = mutableStateOf("")
    val hasta = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    /**
     * Instancia de [SessionManager] para obtener datos de sesión del usuario.
     */
    private val sessionManager = SessionManager(application)
    /**
     * Envía la promoción al backend (Google Cloud Run).
     *
     * Convierte la imagen seleccionada a Base64 si existe y construye
     * un objeto [AgregarPromocionRequest] con los datos del formulario.
     * Luego realiza la petición HTTP para registrar la promoción.
     *
     * @param context Contexto necesario para convertir la imagen a Base64.
     * @param onSuccess Lambda que se ejecuta cuando la promoción se registra correctamente.
     * @param onError Lambda que se ejecuta cuando ocurre un error, con el mensaje de error como parámetro.
     */
    fun guardarPromocion(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val idNegocio = sessionManager.getNegocioId()
                if (idNegocio == null || idNegocio <= 0) {
                    onError("No se encontró el ID del negocio en la sesión.")
                    return@launch
                }

                // Convierte la imagen a base64 (si existe)
                val imagenBase64 = uri.value?.let { ImageUtils.uriToBase64(context, it) } ?: ""

                // Crea el request con los nombres correctos del backend
                val promocion = AgregarPromocionRequest(
                    id_negocio = idNegocio,
                    titulo = nombre.value.ifBlank { "Sin título" },
                    descripcion = descripcion.value.ifBlank { "Sin descripción" },
                    descuento = descuento.value.ifBlank { "Sin descuento" },
                    disponible_desde = desde.value.ifBlank { "22/10/2025" },
                    hasta = hasta.value.ifBlank { "31/10/2025" },
                    imagen = imagenBase64
                )

                Log.d("API_REQUEST", "📤 Enviando promoción: ${promocion}")

                val response = ServicioRemotoAgregarPromocion.api.agregarPromocion(promocion)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = " Error al registrar promoción: ${response.code()} → ${errorBody ?: "sin detalle"}"
                    Log.e("API_ERROR", errorMsg)
                    onError(errorMsg)
                }

            } catch (e: Exception) {
                Log.e("API_EXCEPTION", e.message ?: "Error desconocido")
                val mensaje = ErrorHandler.obtenerMensajeError(e)
                onError(mensaje)
            }
        }
    }
}