package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AyudaPage(navController: NavController) {
    Scaffold(
        topBar = {
            ArrowTopBar(navController, "Ayuda")
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Preguntas frecuentes",
                color = Color(0xFF9605F7),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))

            PreguntaFrecuente(
                pregunta = "¿Cómo puedo cambiar mi contraseña?",
                respuesta = "En tu perfil, selecciona 'Cambiar contraseña' e ingresa tus nuevos datos."
            )
            PreguntaFrecuente(
                pregunta = "¿Dónde puedo actualizar mi información personal?",
                respuesta = "Por ahora sólo puedes visualizar tus datos. Pronto estará disponible la edición."
            )
            PreguntaFrecuente(
                pregunta = "¿Qué hago si la app no carga?",
                respuesta = "Verifica tu conexión a internet o intenta cerrar y abrir la aplicación nuevamente."
            )
        }
    }
}

@Composable
fun PreguntaFrecuente(pregunta: String, respuesta: String) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F3FF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expandido = !expandido }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pregunta,
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expandido) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = null,
                    tint = Color(0xFF9605F7)
                )
            }

            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = respuesta,
                    color = Color(0xFF5B5B5B),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}