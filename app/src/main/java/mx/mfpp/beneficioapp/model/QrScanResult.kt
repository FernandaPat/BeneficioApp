package mx.mfpp.beneficioapp.model

data class QrScanResult(
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
