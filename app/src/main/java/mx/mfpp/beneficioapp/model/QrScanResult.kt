package mx.mfpp.beneficioapp.model

/**
 * Representa el resultado de un escaneo QR.
 *
 * Almacena el contenido del código QR y la marca de tiempo del escaneo.
 *
 * @property content Contenido textual del código QR escaneado
 * @property timestamp Marca de tiempo del momento del escaneo en milisegundos
 */
data class QrScanResult(
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
