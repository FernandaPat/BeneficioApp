package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.QrScanResult

/**
 * ViewModel para manejar la funcionalidad de escaneo QR
 */
class ScannerViewModel : ViewModel() {

    private val _qrScanResults = MutableStateFlow<List<QrScanResult>>(emptyList())
    val qrScanResults: StateFlow<List<QrScanResult>> = _qrScanResults.asStateFlow()

    private val _showScanner = MutableStateFlow(false)
    val showScanner: StateFlow<Boolean> = _showScanner.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun hideScanner() {
        _showScanner.value = false
    }

    fun showScanner() {
        _showScanner.value = true
    }

    fun resetScannerState() {
        _showScanner.value = false
    }

    fun addQrScanResult(content: String) {
        val newList = _qrScanResults.value.toMutableList()
        newList.add(QrScanResult(content))
        _qrScanResults.value = newList
        hideScanner()
    }

    fun deleteQrScanResult(result: QrScanResult) {
        val newList = _qrScanResults.value.toMutableList()
        newList.remove(result)
        _qrScanResults.value = newList
    }

    fun getTotalScans(): Int {
        return _qrScanResults.value.size
    }

    fun clearError() {
        _error.value = null
    }
}