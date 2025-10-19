package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

}