package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

/**
 * Pantalla para solicitar una tarjeta física del beneficio joven.
 *
 * Permite al usuario iniciar el proceso de solicitud de tarjeta física
 * y seleccionar fechas para recogerla.
 *
 * @param navController Controlador de navegación para manejar cambios de pantalla
 */
@Composable
fun SolicitudPage(navController: NavController) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            TextoTitulo("Solicitar Tarjeta Física")
            Spacer(modifier = Modifier.height(55.dp))

            TextoMedioBold("Selecciona una de las siguientes fechas para acudir a recibir tu tarjeta física:")
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BotonMorado(navController, "Realizar Solicitud", Pantalla.RUTA_MAPA_APP)
            }
        }
    }
}

/**
 * Previsualización de la pantalla de solicitud de tarjeta física.
 */
@Preview(showBackground = true)
@Composable
fun SolicitudPagePrev() {
    val navController = rememberNavController()
    SolicitudPage(navController)
}