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
import mx.mfpp.beneficioapp.viewmodel.VerDatosNegocioViewModel
/**
 * Pantalla que muestra los datos registrados de un negocio.
 *
 * La pantalla permite al usuario visualizar información básica del negocio,
 * incluyendo nombre, correo, teléfono y dirección. Maneja estados de carga,
 * error y muestra la información en tarjetas estilizadas.
 *
 * Funcionalidades:
 * - Carga los datos del negocio usando [VerDatosNegocioViewModel].
 * - Muestra un indicador de carga mientras se obtienen los datos.
 * - Muestra un mensaje de error si falla la carga.
 * - Presenta la información del negocio en una Card con estilo consistente.
 *
 * @param navController Controlador de navegación para moverse entre pantallas.
 * @param vm ViewModel que maneja la lógica de obtención de los datos del negocio.
 */
@Composable
fun VerDatosNegocioPage(navController: NavController, vm: VerDatosNegocioViewModel = viewModel()) {
    val context = LocalContext.current
    val negocio = vm.negocio.collectAsState()
    val cargando = vm.cargando.collectAsState()
    val error = vm.error.collectAsState()

    LaunchedEffect(Unit) {
        vm.cargarDatos(context)
    }

    Scaffold(
        topBar = { ArrowTopBar(navController, "Datos del Negocio") },
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

                negocio.value != null -> {
                    Text(
                        text = "Consulta la información registrada de tu negocio.",
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
                            DatoNegocioItem("Nombre", negocio.value!!.nombre)
                            DatoNegocioItem("Correo electrónico", negocio.value!!.correo)
                            DatoNegocioItem("Teléfono", negocio.value!!.telefono)
                            DatoNegocioItem("Dirección", negocio.value!!.direccion)
                        }
                    }
                }
            }
        }
    }
}
/**
 * Componente reutilizable para mostrar un campo de información del negocio.
 *
 * Cada item muestra un label en morado y el valor correspondiente debajo.
 *
 * @param label Nombre del campo a mostrar (ej. "Correo electrónico").
 * @param value Valor asociado al campo.
 */
@Composable
fun DatoNegocioItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, color = Color(0xFF9605F7))
        Text(text = value, color = Color.Black)
    }
}
