package mx.mfpp.beneficioapp.view

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import mx.mfpp.beneficioapp.R
import java.util.concurrent.Executors

/**
 * Pantalla de escaneo de códigos QR.
 *
 * Utiliza la cámara del dispositivo y ML Kit para escanear códigos QR
 * y procesar su contenido.
 *
 * @param onQrScanned Callback invocado cuando se escanea un código QR exitosamente
 * @param onBack Callback para volver a la pantalla anterior
 * @param navController Controlador de navegación para manejar la navegación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerQrScreen(
    onQrScanned: (String) -> Unit,
    onBack: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var qrAlreadyScanned by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Escanear QR",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            // Inicia configuración de la cámara
            LaunchedEffect(Unit) {
                if (!qrAlreadyScanned) {
                    setupCamera(context, lifecycleOwner, previewView, onQrScanned) {
                        qrAlreadyScanned = true
                    }
                }
            }

            // Vista previa de la cámara
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Mensaje de ayuda
            Text(
                text = "Enfoca un código QR para escanear",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
            )
        }
    }
}

/**
 * Configura la cámara y el analizador de códigos QR.
 *
 * @param context Contexto de la aplicación
 * @param lifecycleOwner Propietario del ciclo de vida para la cámara
 * @param previewView Vista para mostrar la previsualización de la cámara
 * @param onQrScanned Callback invocado cuando se detecta un código QR
 * @param onScanComplete Callback invocado cuando se completa el escaneo
 */
@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun setupCamera(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    onQrScanned: (String) -> Unit,
    onScanComplete: () -> Unit = {}
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val options = com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val scanner: BarcodeScanner = BarcodeScanning.getClient(options)
        var isProcessing = false

        imageAnalysis.setAnalyzer(
            Executors.newSingleThreadExecutor()
        ) { imageProxy ->
            if (isProcessing) {
                imageProxy.close()
                return@setAnalyzer
            }

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                isProcessing = true

                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            barcode.rawValue?.let { qrContent ->
                                Log.d("MLKit", "QR escaneado: $qrContent")
                                onQrScanned(qrContent)
                                onScanComplete()
                                cameraProvider.unbindAll()
                                return@addOnSuccessListener
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MLKit", "Error escaneando QR", exception)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                        isProcessing = false
                    }
            } else {
                imageProxy.close()
                isProcessing = false
            }
        }

        try {
            cameraProvider.unbindAll()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e("CameraSetup", "Error al configurar cámara", e)
        }
    }, ContextCompat.getMainExecutor(context))
}