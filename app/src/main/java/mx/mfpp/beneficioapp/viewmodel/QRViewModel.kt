package mx.mfpp.beneficioapp.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import mx.mfpp.beneficioapp.model.PromocionData
import org.json.JSONObject
import java.util.EnumMap

class QRViewModel : ViewModel() {

    private val _promocionData = mutableStateOf<PromocionData?>(null)
    val promocionData: State<PromocionData?> = _promocionData

    private val _qrBitmap = mutableStateOf<Bitmap?>(null)
    val qrBitmap: State<Bitmap?> = _qrBitmap

    // Datos mock de la tarjeta del usuario
    private val userCardNumber = "1234567890123456"

    fun aplicarPromocion(nombrePromocion: String) {
        // Generar datos mock para el QR
        val nuevaPromocion = PromocionData(
            numeroTarjeta = userCardNumber,
            fecha = "15/10/2025",
            nombrePromocion = nombrePromocion,
        )

        _promocionData.value = nuevaPromocion
        _qrBitmap.value = generateQRCode(nuevaPromocion)
    }

    fun clearPromocionData() {
        _promocionData.value = null
        _qrBitmap.value = null
    }

    private fun generateQRCode(promocionData: PromocionData): Bitmap {
        return try {
            // Crear JSON válido
            val jsonData = JSONObject().apply {
                put("numeroTarjeta", promocionData.numeroTarjeta)
                put("fecha", promocionData.fecha)
                put("nombrePromocion", promocionData.nombrePromocion)
            }.toString()

            // Resto del código para generar el QR...
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.Q
            hints[EncodeHintType.MARGIN] = 2

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(jsonData, BarcodeFormat.QR_CODE, 512, 512, hints)

            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val moradoColor = 0xFF9605F7.toInt()

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val color = if (bitMatrix[x, y]) moradoColor else 0xFFFFFFFF.toInt()
                    bitmap.setPixel(x, y, color)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            generateSimpleQRCode()
        }
    }

    private fun generateSimpleQRCode(): Bitmap {
        val size = 512
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        // Color morado para el QR - #9605F7
        val moradoColor = 0xFF9605F7.toInt()

        // Crear un patrón de QR simple en morado
        for (x in 0 until size) {
            for (y in 0 until size) {
                // Patrón de cuadrados simulando un QR
                val isMorado = when {
                    // Bordes gruesos
                    x < 40 || x >= size - 40 || y < 40 || y >= size - 40 -> true
                    // Cuadrados de esquina (típicos en QR)
                    (x in 40..120 && y in 40..120) -> true
                    (x in size-120..size-40 && y in 40..120) -> true
                    (x in 40..120 && y in size-120..size-40) -> true
                    // Patrón interno alternado
                    (x / 20) % 2 == 0 && (y / 20) % 2 == 0 -> true
                    else -> false
                }

                val color = if (isMorado) {
                    moradoColor // Morado
                } else {
                    0xFFFFFFFF.toInt() // Blanco
                }
                bitmap.setPixel(x, y, color)
            }
        }
        return bitmap
    }
}