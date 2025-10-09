package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun NotificacionPage(navController: NavController) {
    Scaffold(
        topBar = { ArrowTopBar(navController, "Notificaciones") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Lista de notificaciones según tu diseño
            NotificationItem("Cupón 1", "Nueva", "Establecimiento")
            NotificationItem("Cupón 1", "Nueva", "Establecimiento")
            NotificationItem("Cupón 1", "Expira pronto", "Establecimiento")
            NotificationItem("Cupón 1", "Nueva", "Establecimiento")
        }
    }
}

@Composable
fun NotificationItem(cupon: String, estado: String, establecimiento: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(106.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenCupon()

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = cupon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = establecimiento,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Etiqueta de estado con tamaño fijo para todas
            Box(
                modifier = Modifier
                    .width(100.dp) // Mismo ancho para todas las etiquetas
                    .height(28.dp)
                    .background(
                        color = when (estado.lowercase()) {
                            "nueva" -> Color(0xFF7AF1A7)
                            "expira pronto" -> Color(0xFFFFA500)
                            else -> Color(0xFF7AF1A7)
                        },
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = estado.uppercase(),
                    fontSize = when (estado.lowercase()) {
                        "expira pronto" -> 9.sp // Tamaño ligeramente menor para "EXPIRA PRONTO"
                        else -> 10.sp
                    },
                    color = when (estado.lowercase()) {
                        "nueva" -> Color(0xFF008033)
                        "expira pronto" -> Color(0xFF8B4513)
                        else -> Color(0xFF008033)
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ImagenCupon() {
    Card(
        modifier = Modifier
            .size(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Img",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificacionPreview() {
    val navController = rememberNavController()
    NotificacionPage(navController)
}