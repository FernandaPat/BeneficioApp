package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.viewmodel.IniciarSesionNegocioViewModel

/**
 * Pantalla de inicio de sesión para negocios afiliados al programa Beneficio Joven.
 *
 * Permite que los establecimientos registrados accedan con su correo o número de teléfono,
 * junto con su contraseña. Incluye la opción de recuperación de contraseña y acceso directo
 * al panel de promociones del negocio tras iniciar sesión.
 */
@Composable
fun Iniciar_Sesion_Negocio(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: IniciarSesionNegocioViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val login = viewModel.login.value

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
            // === CORREO O TELÉFONO ===
            Etiqueta("Correo", true)
            CapturaTexto(
                placeholder = "Escribe aquí",
                value = login.correo,
                onValueChange = viewModel::onCorreoChange
            )

            // === CONTRASEÑA ===
            Etiqueta("Contraseña", true)
            BeneficioPasswordField(
                value = login.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Mín. 8 caracteres"
            )

            // === RECUPERAR CONTRASEÑA ===
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

            // === BOTÓN INICIAR SESIÓN ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BotonMorado(
                    navController = navController,
                    texto = "Iniciar Sesión",
                    route = if (viewModel.esFormularioValido())
                        Pantalla.RUTA_INICIONEGOCIO_APP
                    else "",
                    habilitado = viewModel.esFormularioValido()
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Iniciar_Sesion_NegocioPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        Iniciar_Sesion_Negocio(navController)
    }
}
