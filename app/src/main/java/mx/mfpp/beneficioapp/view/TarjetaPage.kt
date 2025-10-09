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

@Composable
fun TarjetaPage(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.
            background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            TextoTitulo("Tu tarjeta digital")
            Spacer(modifier = Modifier.height(55.dp))
            CardImage()
            Spacer(modifier = Modifier.height(55.dp))
            TextoMedioGrande("1234 5678 9012 0001", modifier)
            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BotonMorado(navController, "Solicitar tarjeta física", Pantalla.RUTA_SOLICITUD_APP)
                BotonBlanco(navController, "Ver estatus de la solicitud", Pantalla.RUTA_ESTATUS_SOLICITUD_APP)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun TarjetaPagePreview() {
    val navController = rememberNavController()
    TarjetaPage((navController))
}