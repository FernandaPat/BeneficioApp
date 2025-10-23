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

class ScannerViewModel : ViewModel() {
    var lastScannedValue: String? = null

    private val _showScanner = MutableStateFlow(false)
    val showScanner: StateFlow<Boolean> = _showScanner.asStateFlow()

    // Estados para la validación y aplicación
    private val _validationResult = MutableStateFlow<QRValidationResponse?>(null)
    val validationResult: StateFlow<QRValidationResponse?> = _validationResult.asStateFlow()

    private val _isApplying = MutableStateFlow(false)
    val isApplying: StateFlow<Boolean> = _isApplying.asStateFlow()

    private val _applyResult = MutableStateFlow<Boolean?>(null)
    val applyResult: StateFlow<Boolean?> = _applyResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun showScanner() {
        _showScanner.value = true
    }

    fun hideScanner() {
        _showScanner.value = false
    }

    fun resetScannerState() {
        _showScanner.value = false
        lastScannedValue = null
        _validationResult.value = null
        _applyResult.value = null
        _errorMessage.value = null
    }

    // Validar QR remoto
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
                    onError("Error comunicándose con el servidor")
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

                // Guardar el resultado de validación para usarlo después
                _validationResult.value = response

                // Construir JSON para la pantalla de detalles
                val json = JSONObject().apply {
                    put("numeroTarjeta", datos.joven.folio_digital)
                    put("fecha", datos.token_info.generado)
                    put("nombrePromocion", datos.promocion.nombre)
                    // Agregar datos adicionales que necesitaremos para aplicar la promoción
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

    // ✅ AGREGAR: Aplicar promoción
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