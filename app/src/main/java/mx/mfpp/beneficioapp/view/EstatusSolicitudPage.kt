package mx.mfpp.beneficioapp.view

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Pantalla que muestra el estado actual de una solicitud de tarjeta física.
 *
 * Esta pantalla permite a los usuarios visualizar el estatus de su solicitud de tarjeta
 * y brinda la opción de cancelarla en caso necesario. Incluye navegación hacia otras
 * pantallas a través del [NavController].
 *
 * @param navController Controlador de navegación utilizado para gestionar el flujo
 * entre pantallas dentro de la aplicación.
 */
@Composable
fun EstatusSolicitudPage(navController: NavController) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            TextoTitulo("Estatus de la Solicitud")
            Spacer(modifier = Modifier.height(55.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BotonMorado(navController, "Cancelar solicitud", Pantalla.RUTA_TARJETA_APP)
            }
        }
    }
}