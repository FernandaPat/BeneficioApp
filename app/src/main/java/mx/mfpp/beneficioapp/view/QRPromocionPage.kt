package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.mfpp.beneficioapp.viewmodel.QRViewModel

@Composable
fun QRPromocionPage(
    navController: NavController,
    viewModel: QRViewModel,
    idJoven: Int,
    idPromocion: Int
) {
    val moradoQR = Color(0xFF9605F7)
    val fondoBoton = Color(0xFFE9D4FF)
    val textoBoton = Color(0xFF9605F7)

    // Estados observables del ViewModel
    val qrTokenResponse by viewModel.qrTokenResponse.collectAsState()
    val qrBitmap by viewModel.qrBitmap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Generar QR cuando se entra a la pantalla
    LaunchedEffect(Unit) {
        viewModel.generarQR(idJoven, idPromocion)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título principal
            Text(
                text = "Tu Cupón QR",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Información de la promoción (en una Card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = qrTokenResponse?.preview?.promocion ?: "Promoción",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tarjeta: •••• ${qrTokenResponse?.preview?.folio_digital?.takeLast(4) ?: "0000"}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Establecimiento: ${qrTokenResponse?.preview?.establecimiento ?: "-"}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Descripción: ${qrTokenResponse?.preview?.descripcion ?: "-"}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }

            // Estado del QR (loading, error o imagen)
            when {
                isLoading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .size(300.dp)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = moradoQR, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Generando QR...",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }

                error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = error ?: "Error al generar QR",
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                viewModel.clear()
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = fondoBoton,
                                contentColor = textoBoton
                            ),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(56.dp)
                        ) {
                            Text(
                                text = "Volver",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                qrBitmap != null -> {
                    Card(
                        modifier = Modifier
                            .size(300.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = qrBitmap!!.asImageBitmap(),
                                contentDescription = "Código QR",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            // Texto de instrucciones
            Text(
                text = "Muestra este código QR en el establecimiento",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Cancelar
            Button(
                onClick = {
                    viewModel.clear()
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = fondoBoton,
                    contentColor = textoBoton
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text(
                    text = "Cancelar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}
