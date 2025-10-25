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

/**
 * ViewModel encargado de generar códigos QR para promociones.
 */
class QRViewModel : ViewModel() {

    /** Respuesta de la API con el token del QR */
    private val _qrTokenResponse = MutableStateFlow<QRTokenResponse?>(null)
    val qrTokenResponse: StateFlow<QRTokenResponse?> = _qrTokenResponse.asStateFlow()

    /** Bitmap del código QR generado */
    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap.asStateFlow()

    /** Indica si se está generando el QR */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Mensaje de error en caso de fallo */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Genera un código QR para un joven y una promoción específicos.
     *
     * @param idJoven ID del joven
     * @param idPromocion ID de la promoción
     */
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

    /**
     * Genera un Bitmap de un código QR sin margen.
     *
     * @param token Texto a codificar en el QR
     * @return Bitmap del QR generado
     */
    private fun generateQRCodeNoMargin(token: String): Bitmap {
        val size = 500 // tamaño del QR
        val hints = mapOf(
            EncodeHintType.MARGIN to 1, // margen mínimo
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

    /**
     * Limpia los datos del QR y errores.
     */
    fun clear() {
        _qrTokenResponse.value = null
        _qrBitmap.value = null
        _error.value = null
    }
}