package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.mfpp.beneficioapp.viewmodel.ScannerViewModel

/**
 * Pantalla que muestra el historial de escaneos QR realizados por el negocio.
 *
 * Permite ver todos los códigos QR escaneados, eliminar registros individuales
 * y acceder al escáner para realizar nuevos escaneos.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas
 * @param viewModel ViewModel que gestiona el estado y datos de los escaneos QR
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScannerScreen(
    navController: NavController,
    viewModel: ScannerViewModel = viewModel()
) {
    val qrScanResults by viewModel.qrScanResults.collectAsState()
    val totalScans = viewModel.getTotalScans()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Scanner QR",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Navegar de manera segura al inicio de negocio
                        navController.navigate(Pantalla.RUTA_INICIO_NEGOCIO) {
                            popUpTo(Pantalla.RUTA_INICIO_NEGOCIO) { inclusive = true }
                            launchSingleTop = true
                        }
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.showScanner()
                },
                containerColor = Color(0xFF9605F7),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escanear QR",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Tarjeta de información
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Scanner QR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Escanea códigos QR de clientes para registrar sus beneficios",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total de escaneos:",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$totalScans",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF9605F7)
                        )
                    }
                }
            }

            // Lista de escaneos
            if (qrScanResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Sin escaneos",
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No hay escaneos registrados",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Presiona el botón + para comenzar a escanear",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(qrScanResults) { scanResult ->
                        ScanResultCard(
                            scanResult = scanResult,
                            onDelete = { viewModel.deleteQrScanResult(it) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente que representa una tarjeta individual de resultado de escaneo QR.
 *
 * Muestra el contenido del código QR, la fecha/hora del escaneo y permite eliminarlo.
 *
 * @param scanResult Datos del escaneo QR a mostrar
 * @param onDelete Callback invocado cuando se solicita eliminar el escaneo
 */
@Composable
fun ScanResultCard(
    scanResult: mx.mfpp.beneficioapp.model.QrScanResult,
    onDelete: (mx.mfpp.beneficioapp.model.QrScanResult) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = scanResult.content,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Escaneado: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(scanResult.timestamp))}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = { onDelete(scanResult) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Gray
                )
            }
        }
    }
}