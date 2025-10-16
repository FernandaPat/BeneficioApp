package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.mfpp.beneficioapp.viewmodel.QRViewModel

@Composable
fun QRPromocionPage(
    navController: NavController,
    viewModel: QRViewModel
) {
    val moradoQR = Color(0xFF9605F7) // Color morado para el QR
    val rojoBoton = Color(0xFFFF5252) // Color rojo para el botón
    val rojoTexto = Color(0xFFFFFFFF) // Texto blanco para el botón rojo

    // Observar los datos del ViewModel
    val promocionData by viewModel.promocionData
    val qrBitmap by viewModel.qrBitmap

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Título - Más arriba con más espacio
            Text(
                text = "Tu Cupón QR",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp, // Tamaño más grande
                color = Color.Black,
                modifier = Modifier.padding(bottom = 20.dp) // Más espacio abajo
            )

            // Información de la promoción
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp), // Más espacio
                shape = RoundedCornerShape(16.dp), // Bordes más redondeados
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF)) // Fondo morado claro
            ) {
                Column(
                    modifier = Modifier.padding(20.dp) // Más padding interno
                ) {
                    Text(
                        text = promocionData?.nombrePromocion ?: "Promoción",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, // Texto más grande
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tarjeta: •••• ${promocionData?.numeroTarjeta?.takeLast(4) ?: "0000"}",
                        fontSize = 16.sp, // Texto más grande
                        color = Color.Gray
                    )
                    Text(
                        text = "Fecha: ${promocionData?.fecha ?: "15/10/2025"}",
                        fontSize = 16.sp, // Texto más grande
                        color = Color.Gray
                    )
                }
            }

            // Código QR
            if (qrBitmap != null) {
                Card(
                    modifier = Modifier
                        .size(300.dp) // QR más grande
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp), // Bordes más redondeados
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp) // Más elevación
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "Código QR de la promoción",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp) // Padding interno para el QR
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .size(300.dp)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Generando QR...",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                    CircularProgressIndicator(
                        color = moradoQR, // Usar color morado para el loading
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Instrucciones
            Text(
                text = "Muestra este código QR en el establecimiento",
                fontSize = 16.sp, // Texto más grande
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp)) // Más espacio

            // Botón para regresar - Ahora es rojo y dice "Cancelar"
            Button(
                onClick = {
                    viewModel.clearPromocionData()
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = rojoBoton, // Fondo rojo
                    contentColor = rojoTexto // Texto blanco
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp) // Botón más alto
            ) {
                Text(
                    text = "Cancelar", // Texto cambiado a "Cancelar"
                    color = rojoTexto,
                    fontWeight = FontWeight.Bold, // Texto en negrita
                    fontSize = 18.sp // Texto más grande
                )
            }
        }
    }
}