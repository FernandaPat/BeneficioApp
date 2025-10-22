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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.model.SessionManager

/**
 * Pantalla que muestra la tarjeta digital del usuario.
 *
 * Permite al usuario ver su tarjeta digital y acceder a opciones relacionadas
 * como solicitar tarjeta física y ver estatus de solicitudes.
 *
 * @param navController Controlador de navegación para manejar cambios de pantalla
 * @param modifier Modificador de Composable para personalizar el layout
 */
@Composable
fun TarjetaPage(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val folioTarjeta = sessionManager.getFolioTarjeta().toString()
    val nombreJoven = sessionManager.getNombreJoven().toString()
    val apellidosJoven = sessionManager.getApellidosJoven().toString()


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            TextoTitulo("Tu tarjeta digital")
            Spacer(modifier = Modifier.height(55.dp))
            CardImage()
            Spacer(modifier = Modifier.height(55.dp))
            TextoMedioGrande(folioTarjeta , modifier)
            Spacer(modifier = Modifier.height(50.dp))
            TextoMedioGrande("$nombreJoven $apellidosJoven", modifier)
            Spacer(modifier = Modifier.height(50.dp))

        }
    }
}

/**
 * Previsualización de la pantalla de tarjeta digital.
 */
@Preview(showBackground = true)
@Composable
fun TarjetaPagePreview() {
    val navController = rememberNavController()
    TarjetaPage((navController))
}