package mx.mfpp.beneficioapp.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.QRTokenResponse
import java.util.EnumMap

class QRViewModel : ViewModel() {

    private val _qrTokenResponse = MutableStateFlow<QRTokenResponse?>(null)
    val qrTokenResponse: StateFlow<QRTokenResponse?> = _qrTokenResponse.asStateFlow()

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun generarQR(idJoven: Int, idPromocion: Int) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = ServicioRemotoQR.generarQR(idJoven, idPromocion)
                if (response != null && response.success) {
                    _qrTokenResponse.value = response
                    _qrBitmap.value = generateQRCodeNoMargin(response.token)
                } else {
                    _error.value = "No se pudo generar el QR"
                }
            } catch (e: Exception) {
                _error.value = "Error al generar QR: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateQRCodeNoMargin(token: String): Bitmap {
        // Configuración con MARGEN MUY PEQUEÑO
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
        hints[EncodeHintType.MARGIN] = 1 // MARGEN MUY PEQUEÑO (1 pixel)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"

        // Tamaño de matriz
        val matrixSize = 100

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(token, BarcodeFormat.QR_CODE, matrixSize, matrixSize, hints)

        // Crear bitmap directamente desde la matriz
        val bitmap = Bitmap.createBitmap(matrixSize, matrixSize, Bitmap.Config.ARGB_8888)
        val moradoColor = 0xFF9605F7.toInt()

        for (x in 0 until matrixSize) {
            for (y in 0 until matrixSize) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) moradoColor else 0xFFFFFFFF.toInt())
            }
        }

        // Escalar al tamaño final
        return Bitmap.createScaledBitmap(bitmap, 512, 512, true)
    }

    fun clear() {
        _qrTokenResponse.value = null
        _qrBitmap.value = null
        _error.value = null
    }
}