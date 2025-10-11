package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

/**
 * Pantalla de inicio de sesión para negocios afiliados al programa Beneficio Joven.
 *
 * Permite que los establecimientos registrados accedan con su correo o número de teléfono,
 * junto con su contraseña. Incluye la opción de recuperación de contraseña y acceso directo
 * al panel de promociones del negocio tras iniciar sesión.
 *
 * @param navController Controlador de navegación que gestiona las transiciones entre pantallas.
 * @param modifier Modificador opcional para ajustar el diseño visual o comportamiento del contenedor.
 */
@Composable
fun Iniciar_Sesion_Negocio(navController: NavController, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = { ArrowTopBar(navController, "Iniciar Sesión") },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .imePadding()
                .padding(paddingValues)
                .padding(top = 3.dp)
        ) {
            Etiqueta("Correo o número de teléfono",true)
            CapturaTexto("Escribe aquí", 10)

            Etiqueta("Contraseña", true)
            CapturaTexto("Escribe aquí", 16)
            Text(
                text = "Recuperar contraseña",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontSize = 11.sp,
                modifier = Modifier
                    .padding(start = 30.dp)
                    .clickable {
                    navController.navigate(Pantalla.RUTA_INICIAR_SESION)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                BotonMorado(navController, "Iniciar Sesión", Pantalla.RUTA_INICIONEGOCIO_APP)

                Spacer(Modifier.height(16.dp))

            }
        }
    }
}
/**
 * Vista previa del diseño de la pantalla de inicio de sesión de negocios.
 *
 * Permite visualizar la interfaz en el editor de Jetpack Compose sin necesidad
 * de ejecutar la aplicación completa.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Iniciar_Sesion_NegocioPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        Iniciar_Sesion_Negocio(navController)
    }
}