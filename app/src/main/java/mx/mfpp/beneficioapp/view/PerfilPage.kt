package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.R

@Composable
fun PerfilPage(navController: NavController) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    val moradoClaro = Color(0xFFE9D4FF)
    val moradoBoton = Color(0xFFD5A8FF)
    val moradoTexto = Color(0xFF9605F7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Encabezado superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(moradoClaro)
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "Regresar",
                    colorFilter = ColorFilter.tint(Color.Black),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                        .size(24.dp)
                )

                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 45.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "Editar foto",
                color = moradoTexto,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Nombre completo",
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.height(25.dp))

            // Sección: Ajustes de cuenta
            SeccionOpciones(
                titulo = "Ajustes de cuenta",
                opciones = listOf(
                    OpcionData(R.drawable.lock, "Cambiar Contraseña"),
                    OpcionData(R.drawable.user, "Editar datos personales"),
                    OpcionData(R.drawable.logout, "Cerrar Sesión") { mostrarDialogo = true }
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Sección: Más información
            SeccionOpciones(
                titulo = "Más información",
                opciones = listOf(
                    OpcionData(R.drawable.help, "Ayuda"),
                    OpcionData(R.drawable.info, "Acerca de Dirección de Juventud")
                )
            )
        }

        // Diálogo para cerrar sesión
        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                confirmButton = {},
                title = null,
                text = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 25.dp)
                        ) {
                            Text(
                                text = "¿Quieres cerrar la sesión?",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(horizontalArrangement = Arrangement.Center) {
                                Button(
                                    onClick = { mostrarDialogo = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(40.dp),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp, brush = androidx.compose.ui.graphics.SolidColor(moradoTexto))
                                ) {
                                    Text("No", color = moradoTexto)
                                }
                                Spacer(modifier = Modifier.width(25.dp))
                                Button(
                                    onClick = {
                                        mostrarDialogo = false
                                        // acción de cierre de sesión
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = moradoBoton),
                                    shape = RoundedCornerShape(40.dp)
                                ) {
                                    Text("Sí", color = Color.White)
                                }
                            }
                        }
                    }
                },
                containerColor = Color.Transparent
            )
        }
    }
}

data class OpcionData(
    val icono: Int,
    val texto: String,
    val onClick: (() -> Unit)? = null
)

@Composable
fun SeccionOpciones(titulo: String, opciones: List<OpcionData>) {
    val borde = Color(0xFFE5E5E5)

    Text(
        text = titulo,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, bottom = 5.dp),
        color = Color.Black,
        fontWeight = FontWeight.Bold
    )

    Card(
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .padding(horizontal = 25.dp)
            .fillMaxWidth()
    ) {
        Column {
            opciones.forEachIndexed { index, opcion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { opcion.onClick?.invoke() }
                        .padding(vertical = 14.dp, horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = opcion.icono),
                        contentDescription = opcion.texto,
                        modifier = Modifier.size(22.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF88A0A8))
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = opcion.texto,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(Color(0xFFB8B8B8))
                    )
                }
                if (index != opciones.lastIndex) {
                    Divider(color = borde, thickness = 1.dp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilPagePreview() {
    val navController = rememberNavController()
    PerfilPage(navController)
}
