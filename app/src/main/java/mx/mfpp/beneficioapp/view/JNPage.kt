package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.view.Pantalla.Companion.RUTA_INICIAR_SESION_NEGOCIO
import mx.mfpp.beneficioapp.view.Pantalla.Companion.RUTA_LOGIN_APP

/**
 * Pantalla de selección de tipo de usuario que permite elegir entre "Soy joven" y "Soy negocio".
 *
 * Esta pantalla sirve como punto de entrada principal de la aplicación, presentando una interfaz
 * limpia con el logo de la aplicación y dos botones que redirigen a diferentes flujos de la app.
 *
 * Utiliza un diseño centrado con [Box] y [Column] para organizar los elementos visuales
 * de manera vertical y alineada al centro de la pantalla.
 *
 * @param navController Controlador de navegación utilizado para manejar la navegación entre pantallas
 *
 * @see BotonMorado Componente de botón con estilo morado para la opción principal
 * @see BotonBlanco Componente de botón con estilo blanco para la opción secundaria
 */
@Composable
fun JNPage(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Imagen centrada
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de la app",
                modifier = Modifier
                    .size(290.dp)
                    .padding(bottom = 32.dp)
            )
            BotonMorado(navController, "Soy Joven", RUTA_LOGIN_APP)
            Spacer(modifier = Modifier.height(20.dp))
            BotonBlanco(navController, "Soy Negocio",RUTA_INICIAR_SESION_NEGOCIO)
        }
    }
}

/**
 * Función de preview para visualizar el diseño de la pantalla JNPage en Android Studio.
 *
 * Permite ver la interfaz de usuario sin necesidad de ejecutar la aplicación completa,
 * mostrando el logo y los botones en un contexto aislado con fondo visible.
 *
 * @see Preview Anotación que habilita la visualización en el panel de diseño de Android Studio
 */
@Preview(showBackground = true)
@Composable
fun JNPagePreview() {
    val navController = rememberNavController()
    JNPage(navController)
}