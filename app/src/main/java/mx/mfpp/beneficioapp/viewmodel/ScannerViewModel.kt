package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.PromocionData
import mx.mfpp.beneficioapp.model.QrScanResult
import org.json.JSONObject

/**
 * ViewModel para manejar la funcionalidad de escaneo QR
 */
class ScannerViewModel : ViewModel() {
    var lastScannedValue: String? = null

    private val _qrScanResults = MutableStateFlow<List<QrScanResult>>(emptyList())
    val qrScanResults: StateFlow<List<QrScanResult>> = _qrScanResults.asStateFlow()

    private val _showScanner = MutableStateFlow(false)
    val showScanner: StateFlow<Boolean> = _showScanner.asStateFlow()

    fun processScannedQR(content: String): PromocionData? {
        return try {
            val json = JSONObject(content) // <-- Sin decodificar
            PromocionData(
                numeroTarjeta = json.optString("numeroTarjeta", "N/A"),
                fecha = json.optString("fecha", "N/A"),
                nombrePromocion = json.optString("nombrePromocion", "Promoción desconocida")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun showScanner() {
        _showScanner.value = true
    }

    fun hideScanner() {
        _showScanner.value = false
    }

    fun addQrScanResult(content: String) {
        val newList = _qrScanResults.value.toMutableList()
        newList.add(QrScanResult(content))
        _qrScanResults.value = newList
    }

    fun deleteQrScanResult(result: QrScanResult) {
        val newList = _qrScanResults.value.toMutableList()
        newList.remove(result)
        _qrScanResults.value = newList
    }

    fun getTotalScans(): Int {
        return _qrScanResults.value.size
    }
    fun resetScannerState() {
        _showScanner.value = false
        lastScannedValue = null
    }

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
                    // backend puede devolver message explicativo
                    onError(response.message)
                    return@launch
                }

                val datos = response.datos
                if (datos == null) {
                    onError("Respuesta inválida del servidor")
                    return@launch
                }

                // Construimos un JSON simple compatible con tu DetallePromocionScreen actual:
                // { "numeroTarjeta": "<folio_digital>", "fecha": "<generado>", "nombrePromocion": "<nombre>" }
                val json = org.json.JSONObject().apply {
                    put("numeroTarjeta", datos.joven.folio_digital)
                    put("fecha", datos.token_info.generado)
                    put("nombrePromocion", datos.promocion.nombre)
                }.toString()

                // Codifica para pasar por la navRoute (igual que hacías antes)
                val encoded = java.net.URLEncoder.encode(json, "UTF-8")
                onSuccess(encoded)
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Excepción: ${e.message ?: "Desconocida"}")
            }
        }
    }
}