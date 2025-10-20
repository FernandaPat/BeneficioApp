package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CambiarContrasenaPage(navController: NavController) {
    Scaffold(
        topBar = {
            ArrowTopBar(navController, "Cambiar Contraseña")
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Actualiza tu contraseña para mantener tu cuenta segura.",
                color = Color(0xFF5B5B5B),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            var contraActual by rememberSaveable { mutableStateOf("") }
            var contraNueva by rememberSaveable { mutableStateOf("") }
            var contraConfirm by rememberSaveable { mutableStateOf("") }

            Etiqueta("Contraseña actual", true)
            BeneficioPasswordField(
                value = contraActual,
                onValueChange = { contraActual = it },
                placeholder = "Escribe tu contraseña actual"
            )
            Spacer(modifier = Modifier.height(16.dp))

            Etiqueta("Nueva contraseña", true)
            BeneficioPasswordField(
                value = contraNueva,
                onValueChange = { contraNueva = it },
                placeholder = "Mínimo 8 caracteres"
            )
            Spacer(modifier = Modifier.height(16.dp))

            Etiqueta("Confirmar nueva contraseña", true)
            BeneficioPasswordField(
                value = contraConfirm,
                onValueChange = { contraConfirm = it },
                placeholder = "Repite la nueva contraseña"
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Lógica de cambio */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9605F7))
            ) {
                Text("Guardar cambios", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}