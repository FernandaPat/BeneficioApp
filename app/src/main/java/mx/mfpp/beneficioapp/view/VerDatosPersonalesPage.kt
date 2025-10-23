package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.mfpp.beneficioapp.viewmodel.VerDatosPersonalesViewModel

@Composable
fun VerDatosPersonalesPage(navController: NavController, vm: VerDatosPersonalesViewModel = viewModel()) {
    val context = LocalContext.current
    val joven = vm.joven.collectAsState()
    val cargando = vm.cargando.collectAsState()
    val error = vm.error.collectAsState()


    // ✅ Llamar automáticamente al cargar la pantalla
    LaunchedEffect(Unit) {
        vm.cargarDatos()
    }


    Scaffold(
        topBar = { ArrowTopBar(navController, "Datos Personales") },
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
            when {
                cargando.value -> {
                    CircularProgressIndicator(color = Color(0xFF9605F7))
                }

                error.value != null -> {
                    Text(text = "⚠️ ${error.value}", color = Color.Red)
                }

                joven.value != null -> {
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
                            DatoPersonalItem("Nombre", joven.value!!.nombre)
                            DatoPersonalItem("Correo electrónico", joven.value!!.correo)
                            DatoPersonalItem("Teléfono", joven.value!!.telefono)
                            DatoPersonalItem("Dirección", joven.value!!.direccion)
                        }
                    }

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
