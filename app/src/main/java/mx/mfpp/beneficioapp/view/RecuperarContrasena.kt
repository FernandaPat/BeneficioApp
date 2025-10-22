package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.viewmodel.RecuperarContrasenaViewModel
import mx.mfpp.beneficioapp.viewmodel.RecuperarState

@Composable
fun RecuperarContrasena(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RecuperarContrasenaViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val email = viewModel.email.value
    val recuperarState by viewModel.recuperarState.collectAsStateWithLifecycle()
    val moradoTexto = Color(0xFF9605F7)

    // Manejar estados
    LaunchedEffect(recuperarState) {
        when (recuperarState) {
            is RecuperarState.Success -> {
                // Después de mostrar éxito, esperar y regresar
                kotlinx.coroutines.delay(3000) // 3 segundos
                viewModel.resetState()
                navController.popBackStack()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = { ArrowTopBar(navController, "Recuperar Contraseña") }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .imePadding()
                .padding(paddingValues)
                .padding(horizontal = 30.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(40.dp))

            // Icono de email
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email",
                modifier = Modifier.size(120.dp),
                tint = Color(0xFF9605F7)
            )

            Spacer(Modifier.height(30.dp))

            // Título
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Descripción
            Text(
                text = "Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(Modifier.height(40.dp))

            // Campo de correo
            Etiqueta("Correo Electrónico", true)
            CapturaTexto(
                placeholder = "ejemplo@correo.com",
                value = email,
                onValueChange = viewModel::onEmailChange
            )

            Spacer(Modifier.height(40.dp))

            // Mostrar mensaje de error si hay
            if (recuperarState is RecuperarState.Error) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = (recuperarState as RecuperarState.Error).message,
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Mostrar mensaje de éxito
            if (recuperarState is RecuperarState.Success) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✅ ¡Correo enviado!",
                            color = Color(0xFF2E7D32),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Revisa tu bandeja de entrada y sigue las instrucciones para restablecer tu contraseña.",
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Botón de enviar
            BotonMorado(
                texto = if (recuperarState is RecuperarState.Loading) "Enviando..." else "Enviar",
                onClick = { viewModel.solicitarRecuperacion() },
                habilitado = viewModel.esEmailValido() && recuperarState !is RecuperarState.Loading,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Botón para regresar
            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text(
                    text = "Volver al inicio de sesión",
                    color = moradoTexto,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecuperarContrasenaPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        RecuperarContrasena(navController)
    }
}
