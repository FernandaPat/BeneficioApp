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

/**
 * Pantalla de **inicio de sesión** de usuarios dentro de BeneficioApp.
 *
 * Esta función Composable muestra el formulario de login para los usuarios tipo “joven”.
 * Se conecta al [IniciarSesionViewModel] para gestionar el estado del formulario y las respuestas
 * del servidor, mostrando mensajes de error o navegando a la pantalla principal en caso de éxito.
 *
 * El diseño incluye campos de correo y contraseña, un botón de inicio de sesión
 * y accesos para crear cuenta o recuperar la contraseña.
 *
 * @param navController Controlador de navegación utilizado para cambiar de pantalla.
 * @param modifier Permite aplicar modificadores de estilo o layout al componente raíz.
 * @param viewModel ViewModel asociado que maneja la lógica de autenticación.
 */
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

    /**
     * Observa los cambios del estado de inicio de sesión y ejecuta acciones
     * según el resultado (navegación o mensaje de error).
     */
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
            Etiqueta("Correo o número de teléfono", true)
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
                        navController.navigate(Pantalla.RUTA_INICIAR_SESION)
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

/**
 * Botón personalizado de color morado usado en el flujo de autenticación.
 *
 * Este componente reutilizable centraliza el estilo visual del botón principal,
 * aplicando colores institucionales y un diseño redondeado.
 *
 * @param texto Texto mostrado dentro del botón.
 * @param onClick Acción que se ejecuta al presionar el botón.
 * @param modifier Modificador opcional para personalizar su tamaño o posición.
 * @param habilitado Define si el botón se puede presionar.
 */
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
/**
 * Vista previa de la pantalla de inicio de sesión.
 *
 * Permite visualizar el diseño en Android Studio sin ejecutar la aplicación.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Iniciar_SesionPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        Iniciar_Sesion(navController)
    }
}
