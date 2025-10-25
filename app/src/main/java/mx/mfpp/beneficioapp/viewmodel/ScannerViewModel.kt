package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.QRValidationResponse
import mx.mfpp.beneficioapp.model.ServicioRemotoQR
import org.json.JSONObject
/**
 * ViewModel encargado de la lógica de escaneo, validación y aplicación de códigos QR.
 */
class ScannerViewModel : ViewModel() {

    /** Último valor escaneado del QR */
    var lastScannedValue: String? = null

    /** Controla si la UI debe mostrar la cámara para escaneo */
    private val _showScanner = MutableStateFlow(false)
    val showScanner: StateFlow<Boolean> = _showScanner.asStateFlow()

    /** Resultado de la validación remota del QR */
    private val _validationResult = MutableStateFlow<QRValidationResponse?>(null)
    val validationResult: StateFlow<QRValidationResponse?> = _validationResult.asStateFlow()

    /** Indica si se está aplicando una promoción */
    private val _isApplying = MutableStateFlow(false)
    val isApplying: StateFlow<Boolean> = _isApplying.asStateFlow()

    /** Resultado de la aplicación de promoción (true = éxito, false = fallo) */
    private val _applyResult = MutableStateFlow<Boolean?>(null)
    val applyResult: StateFlow<Boolean?> = _applyResult.asStateFlow()

    /** Mensaje de error actual */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /** Muestra el scanner */
    fun showScanner() {
        _showScanner.value = true
    }

    /** Oculta el scanner */
    fun hideScanner() {
        _showScanner.value = false
    }

    /** Reinicia todos los estados del scanner */
    fun resetScannerState() {
        _showScanner.value = false
        lastScannedValue = null
        _validationResult.value = null
        _applyResult.value = null
        _errorMessage.value = null
    }

    /**
     * Valida el QR en el servidor.
     *
     * @param token Token del QR escaneado.
     * @param idEstablecimiento ID del establecimiento.
     * @param onSuccess Callback con datos codificados si la validación es exitosa.
     * @param onError Callback con mensaje de error si la validación falla.
     */
    fun validarQrRemoto(
        token: String,
        idEstablecimiento: Int,
        onSuccess: (encodedQrData: String) -> Unit,
        onError: (errorMsg: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = ServicioRemotoQR.validarQR(token, idEstablecimiento)
                if (response == null) {
                    onError("Error al escanear QR")
                    return@launch
                }

                if (!response.success) {
                    onError(response.message ?: "Error al validar QR")
                    return@launch
                }

                val datos = response.datos
                if (datos == null) {
                    onError("Respuesta inválida del servidor")
                    return@launch
                }

                // Guardar el resultado de validación
                _validationResult.value = response

                // Construir JSON para la pantalla de detalles
                val json = JSONObject().apply {
                    put("numeroTarjeta", datos.joven.folio_digital)
                    put("fecha", datos.token_info.generado)
                    put("nombrePromocion", datos.promocion.nombre)
                    put("id_tarjeta", datos.joven.id_tarjeta)
                    put("id_promocion", datos.promocion.id_promocion)
                    put("id_establecimiento", idEstablecimiento)
                }.toString()

                val encoded = java.net.URLEncoder.encode(json, "UTF-8")
                onSuccess(encoded)
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Error de conexión: ${e.message ?: "Intenta nuevamente"}")
            }
        }
    }

    /**
     * Aplica la promoción escaneada.
     *
     * @param idTarjeta ID de la tarjeta del joven.
     * @param idPromocion ID de la promoción.
     * @param idEstablecimiento ID del establecimiento.
     * @param onSuccess Callback si la aplicación es exitosa.
     * @param onError Callback con mensaje de error si falla.
     */
    fun aplicarPromocion(
        idTarjeta: Int,
        idPromocion: Int,
        idEstablecimiento: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isApplying.value = true
            _errorMessage.value = null

            try {
                val response = ServicioRemotoQR.aplicarPromocion(
                    idTarjeta,
                    idPromocion,
                    idEstablecimiento
                )

                if (response != null && response.success) {
                    _applyResult.value = true
                    onSuccess()
                } else {
                    _applyResult.value = false
                    onError("No se pudo aplicar la promoción")
                }
            } catch (e: Exception) {
                _applyResult.value = false
                _errorMessage.value = e.message
                onError("Error: ${e.message ?: "Intenta nuevamente"}")
            } finally {
                _isApplying.value = false
            }
        }
    }
}