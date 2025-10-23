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
                        else "Detalles de promoción",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                scrollBehavior = null
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDFDFD))
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when {
                isApplying -> {
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
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(90.dp)
                        )

                        Text(
                            text = "¡Promoción Aplicada!",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            textAlign = TextAlign.Center
                        )

                        if (promocionData != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF)),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = promocionData["nombrePromocion"] as String,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9605F7)
                                    )

                                    InfoRow("Número de tarjeta:", "•••• ${(promocionData["numeroTarjeta"] as String).takeLast(4)}")
                                    InfoRow("Fecha:", promocionData["fecha"] as String)
                                    InfoRow("Establecimiento:", sessionManager.getNombreNegocio() ?: "-")
                                }
                            }
                        }

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE9d4ff),
                                contentColor = Color(0xFF9605f7)
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Text("Aceptar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                applyResult == false -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(90.dp)
                        )
                        Text(
                            "Error al activar promoción",
                            color = Color.Red,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            errorMessage ?: "No se pudo activar la promoción",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                viewModel.resetScannerState()
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9605F7),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Volver", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                else -> {
                    if (promocionData != null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF)),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = promocionData["nombrePromocion"] as String,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9605F7)
                                    )

                                    InfoRow("Número de tarjeta:", "•••• ${(promocionData["numeroTarjeta"] as String).takeLast(4)}")
                                    InfoRow("Fecha:", promocionData["fecha"] as String)
                                    InfoRow("Establecimiento:", sessionManager.getNombreNegocio() ?: "-")
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = {
                                    viewModel.aplicarPromocion(
                                        idTarjeta = promocionData["id_tarjeta"] as Int,
                                        idPromocion = promocionData["id_promocion"] as Int,
                                        idEstablecimiento = promocionData["id_establecimiento"] as Int,
                                        onSuccess = {},
                                        onError = {}
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE9d4ff),
                                    contentColor = Color(0xFF9605f7)
                                ),
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                Text("Activar Promoción", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(90.dp)
                            )
                            Text(
                                "Error al leer datos del QR",
                                color = Color.Red,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "El código QR escaneado no es válido",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.fillMaxWidth().padding(15.dp),
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE9d4ff),
                                    contentColor = Color(0xFF9605f7)
                                )
                            ) {
                                Text("Volver al escáner", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp)) // espacio pequeño
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}