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
import mx.mfpp.beneficioapp.model.ServicioRemotoQR
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
            val (response, errorMessage) = ServicioRemotoQR.generarQR(idJoven, idPromocion)

            if (response != null) {
                _qrTokenResponse.value = response
                _qrBitmap.value = generateQRCodeNoMargin(response.token)
            } else {
                _error.value = errorMessage ?: "No se pudo generar el QR."
            }

            _isLoading.value = false
        }
    }


    private fun generateQRCodeNoMargin(token: String): Bitmap {
        val size = 500 // tamaño final del QR (ajústalo a gusto)
        val hints = mapOf(
            EncodeHintType.MARGIN to 1, // un poco de aire alrededor
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )

        val bitMatrix = QRCodeWriter().encode(token, BarcodeFormat.QR_CODE, size, size, hints)
        val moradoColor = 0xFF9605F7.toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) moradoColor else 0xFFFFFFFF.toInt())
            }
        }

        return bitmap
    }

    fun clear() {
        _qrTokenResponse.value = null
        _qrBitmap.value = null
        _error.value = null
    }
}