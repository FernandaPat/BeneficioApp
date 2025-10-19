package mx.mfpp.beneficioapp.view

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.mfpp.beneficioapp.R

@Composable
fun AcercaDePage(navController: NavController) {
    Scaffold(
        topBar = {
            ArrowTopBar(navController, "Acerca de")
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Dirección de Juventud",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Dirección de Juventud\nAtizapán de Zaragoza",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9605F7),
                fontSize = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F3FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "El programa **Beneficio Joven** busca impulsar el desarrollo de las y los jóvenes de Atizapán de Zaragoza, brindando apoyos, talleres y oportunidades para su crecimiento académico, profesional y personal.",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "📍 Dirección: Blvd. Adolfo López Mateos #91, Las Alamedas, Atizapán de Zaragoza, Edo. Méx.\n\n☎️ Teléfono: (55) 1234 5678\n\n🌐 Facebook: Dirección de Juventud Atizapán",
                        color = Color(0xFF5B5B5B),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}