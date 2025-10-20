package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun VerDatosPersonalesPage(navController: NavController) {
    Scaffold(
        topBar = {
            ArrowTopBar(navController, "Datos Personales")
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Consulta tu información registrada.",
                color = Color(0xFF5B5B5B),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F3FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DatoPersonalItem("Nombre", "Jaretzy Santiago")
                    DatoPersonalItem("Correo electrónico", "jaretzy@ejemplo.com")
                    DatoPersonalItem("Teléfono", "55 1234 5678")
                    DatoPersonalItem("Dirección", "Atizapán de Zaragoza, Estado de México")
                }
            }
        }
    }
}

@Composable
fun DatoPersonalItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, color = Color(0xFF9605F7))
        Text(text = value, color = Color.Black)
    }
}