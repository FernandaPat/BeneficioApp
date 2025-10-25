package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Pantalla de Ayuda que muestra una lista de preguntas frecuentes (FAQ).
 *
 * @param navController Controlador de navegación que permite volver a la pantalla anterior.
 *
 * Esta pantalla utiliza un [Scaffold] con una barra superior y una lista de
 * preguntas frecuentes que pueden expandirse para mostrar sus respuestas.
 * Cada pregunta se representa con el componente [PreguntaFrecuente].
 */
@Composable
fun AyudaPage(navController: NavController) {
    Scaffold(
        topBar = {
            ArrowTopBar(navController, "Ayuda")
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Preguntas frecuentes",
                color = Color(0xFF9605F7),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))

            PreguntaFrecuente(
                pregunta = "¿Cómo puedo cambiar mi contraseña?",
                respuesta = "Puedes hacerlo desde tu perfil. Ingresa al inicio, seleccciona tu foto de perfil > Cambiar Contraseña, y escribe tu correo para recibir un enlace donde podrás cambiar tu contraseña. Si olvidaste tu contraseña, usa la opción Recuperar Contraseña en la pantalla de inicio de sesión para restablecerla mediante tu correo electrónico registrado."
            )
            PreguntaFrecuente(
                pregunta = "¿Qué hago si la aplicación no carga correctamente?",
                respuesta = "Primero, asegúrate de tener una conexión estable a internet. Si el problema persiste, cierra completamente la app y vuelve a abrirla. También puedes intentar borrar la caché desde la configuración del teléfono."
            )
            PreguntaFrecuente(
                pregunta = "¿Cómo puedo registrar una nueva cuenta?",
                respuesta = "Cierra tu sesión actual y selecciona Soy Joven, después Crear cuenta, llena tus datos personales y verifica tu correo electrónico. Te recomendamos usar una contraseña segura y un correo válido, ya que lo necesitarás para recuperar tu cuenta o recibir notificaciones."
            )
            PreguntaFrecuente(
                pregunta = "¿Qué hago si olvidé mi contraseña?",
                respuesta = "En la pantalla de inicio de sesión, selecciona Recuperar Contraseña, ingresa tu correo electrónico y sigue las instrucciones que recibirás. Si no ves el mensaje en tu bandeja principal, revisa la carpeta de correo no deseado o spam."
            )
            PreguntaFrecuente(
                pregunta = "¿Puedo usar Atizapp sin conexión a internet?",
                respuesta = "Algunas funciones básicas pueden mantenerse visibles sin conexión, como tus datos ya cargados. Sin embargo, para revisar promociones, actualizaciones o ver el mapa, necesitas conexión a internet."
            )
            PreguntaFrecuente(
                pregunta = "¿Por qué no recibo notificaciones?",
                respuesta = "Asegúrate de que las notificaciones estén habilitadas tanto en la configuración de tu dispositivo como dentro de la app. Si aún no las recibes, verifica que tengas la última versión de la app y una conexión activa."
            )
            PreguntaFrecuente(
                pregunta = "¿Por qué no puedo ver el mapa?",
                respuesta = "Asegúrate de que los permisos de ubicación estén habilitados tanto en la configuración de tu dispositivo como dentro de la app. Si aún no las recibes, verifica que tengas la última versión de la app y una conexión activa."
            )
        }
    }
}

/**
 * Componente reutilizable que muestra una pregunta frecuente con su respuesta.
 *
 * @param pregunta Texto de la pregunta que se mostrará en la tarjeta.
 * @param respuesta Texto de la respuesta que se expandirá al hacer clic.
 *
 * Este componente usa una [Card] con animación expandible mediante [AnimatedVisibility].
 * Al presionar la tarjeta, la respuesta se muestra o se oculta con una transición vertical.
 */
@Composable
fun PreguntaFrecuente(pregunta: String, respuesta: String) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F3FF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expandido = !expandido }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pregunta,
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expandido) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = null,
                    tint = Color(0xFF9605F7)
                )
            }

            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = respuesta,
                    color = Color(0xFF5B5B5B),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}