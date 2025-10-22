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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.viewmodel.IniciarSesionViewModel
import mx.mfpp.beneficioapp.viewmodel.LoginState

@Composable
fun Iniciar_Sesion(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: IniciarSesionViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val login = viewModel.login.value

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                // Navega a la pantalla principal y limpia el login del historial
                navController.navigate(Pantalla.RUTA_INICIO_APP) {
                    popUpTo(Pantalla.RUTA_INICIAR_SESION) { inclusive = true }
                }
                viewModel.resetState() // Resetea el estado para la próxima vez
            }
            is LoginState.Error -> {
                // Muestra el mensaje de error en un Snackbar
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState() // Resetea el estado
            }
            else -> Unit // No hacer nada en los estados Idle o Loading
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
            // === CORREO ===
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

            // === RECUPERAR ===
            Text(
                text = "Recuperar contraseña",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontSize = 11.sp,
                modifier = Modifier
                    .padding(start = 30.dp)
                    .clickable {
                        Log.d("Recuperar", "Se clickea pero se rompe")
                        //navController.navigate(/*TODO: PANTALLA PARA RECUMERAR CONTRASEÑA*/)
                    }
            )

            // === BOTÓN ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (loginState is LoginState.Error) {
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                BotonMorado(
                    texto = "Iniciar Sesión",
                    // Aquí le dices al botón qué hacer: llamar al ViewModel
                    onClick = {
                        Log.d("LOGIN_UI_CHECK", "✅ ¡El onClick del Botón Morado FUE EJECUTADO!")
                        viewModel.iniciarSesion()
                              },
                    habilitado = viewModel.esFormularioValido() && loginState !is LoginState.Loading
                )

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿No tienes una cuenta? ", fontSize = 11.sp, color = Color.Black)
                    Text(
                        text = "Crear Cuenta",
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(Pantalla.RUTA_CREAR_CUENTA)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun BotonMorado(
    texto: String,
    onClick: () -> Unit, // 🔹 Ahora es el único responsable de la acción
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    val moradoFuerte = Color(0xFF9605f7)
    val moradoSuave = Color(0xFFE9d4ff)

    Button(
        onClick = onClick, // 🔹 Llama directamente a la función que le pasas
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(containerColor = moradoSuave),
        shape = RoundedCornerShape(50.dp),
        modifier = modifier
            .width(250.dp)
            .height(60.dp)
    ) {
        Text(
            text = texto,
            color = moradoFuerte,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Iniciar_SesionPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        Iniciar_Sesion(navController)
    }
}
