package mx.mfpp.beneficioapp.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.viewmodel.IniciarSesionNegocioViewModel
import mx.mfpp.beneficioapp.viewmodel.LoginStateNegocio

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

    val loginState by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginState) {
        when (val state: LoginStateNegocio = loginState){
            is LoginStateNegocio.Success -> {
                navController.navigate(Pantalla.RUTA_INICIO_NEGOCIO){
                    popUpTo(Pantalla.RUTA_INICIAR_SESION){inclusive = true}
                }
                viewModel.resetState()
            }
            is LoginStateNegocio.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            else -> Unit
        }
    }

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
            // === CORREO  ===
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
                if (loginState is LoginStateNegocio.Error) {
                    Text(
                        text = (loginState as LoginStateNegocio.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                BotonMorado(
                    texto = "Iniciar Sesion",
                    onClick = {
                        Log.d("LOGIN_UI_CHECK", "¡El onClick del Botón Morado FUE EJECUTADO!")
                        viewModel.iniciarSesion()
                    },
                    habilitado = viewModel.esFormularioValido() && loginState !is LoginStateNegocio.Loading
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
