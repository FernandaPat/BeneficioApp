package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.viewmodel.ScannerViewModel
import org.json.JSONObject
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePromocionScreen(
    navController: NavController,
    qrData: String
) {
    val viewModel: ScannerViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val promocionData = remember(qrData) {
        try {
            val decodedData = URLDecoder.decode(qrData, "UTF-8")
            val json = JSONObject(decodedData)
            mapOf(
                "numeroTarjeta" to json.getString("numeroTarjeta"),
                "fecha" to json.getString("fecha"),
                "nombrePromocion" to json.getString("nombrePromocion"),
                "id_tarjeta" to json.getInt("id_tarjeta"),
                "id_promocion" to json.getInt("id_promocion"),
                "id_establecimiento" to json.getInt("id_establecimiento")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val isApplying by viewModel.isApplying.collectAsState()
    val applyResult by viewModel.applyResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (applyResult == true) "Promoción Aplicada"
                        else "Detalles de promoción"
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
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(24.dp)
        ) {
            when {
                isApplying -> {
                    // Estado: Aplicando promoción
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF9605F7),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Activando promoción...",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                }

                applyResult == true -> {
                    // Estado: Éxito - Promoción aplicada
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier
                                .size(80.dp)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "¡Promoción Aplicada!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        if (promocionData != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = promocionData["nombrePromocion"] as String,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9605F7)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    InfoRow("Número de tarjeta:", "•••• ${(promocionData["numeroTarjeta"] as String).takeLast(4)}")
                                    InfoRow("Fecha:", promocionData["fecha"] as String)
                                    InfoRow("Establecimiento:", sessionManager.getNombreNegocio() ?: "-")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9605F7),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Aceptar", fontSize = 16.sp)
                        }
                    }
                }

                applyResult == false -> {
                    // Estado: Error
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Error al activar promoción",
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            errorMessage ?: "No se pudo activar la promoción",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                viewModel.resetScannerState()
                                navController.popBackStack()
                            }
                        ) {
                            Text("Volver")
                        }
                    }
                }

                else -> {
                    // Estado inicial - Mostrar detalles y botón Activar
                    if (promocionData != null) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Información de la promoción
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = promocionData["nombrePromocion"] as String,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9605F7)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    InfoRow("Número de tarjeta:", "•••• ${(promocionData["numeroTarjeta"] as String).takeLast(4)}")
                                    InfoRow("Fecha:", promocionData["fecha"] as String)
                                    InfoRow("Establecimiento:", sessionManager.getNombreNegocio() ?: "-")
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Botón Activar
                            Button(
                                onClick = {
                                    // Llamar al endpoint para aplicar la promoción
                                    viewModel.aplicarPromocion(
                                        idTarjeta = promocionData["id_tarjeta"] as Int,
                                        idPromocion = promocionData["id_promocion"] as Int,
                                        idEstablecimiento = promocionData["id_establecimiento"] as Int,
                                        onSuccess = {
                                            // Éxito - el estado se actualiza automáticamente
                                        },
                                        onError = {
                                            // Error - el estado se actualiza automáticamente
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF9605F7),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Activar Promoción", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Datos inválidos
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Error al leer datos del QR",
                                color = Color.Red,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "El código QR escaneado no es válido",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { navController.popBackStack() }
                            ) {
                                Text("Volver al escáner")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            fontSize = 16.sp
        )
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}
