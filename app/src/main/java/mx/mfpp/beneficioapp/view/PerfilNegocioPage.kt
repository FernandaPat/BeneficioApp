package mx.mfpp.beneficioapp.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.view.Pantalla.Companion.RUTA_INICIO_APP

@Composable
fun PerfilNegocioPage(navController: NavController) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    val moradoClaro = Color(0xFFE9D4FF)
    val moradoBoton = Color(0xFFD5A8FF)
    val moradoTexto = Color(0xFF9605F7)
    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            uri = result
        }

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
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Regresar",
                        modifier = Modifier
                            .size(30.dp),
                        tint = Color.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Fondo redondeado
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                    )

                    if (uri == null) {
                        IconButton(
                            onClick = { pickImage.launch("image/*") },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFF9605F7)
                            ),
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Seleccionar imagen"
                            )
                        }
                    } else {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Imagen de la promoción",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape)
                        )

                        IconButton(
                            onClick = { pickImage.launch("image/*") },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .zIndex(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Cambiar imagen",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Nombre Negocio",
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.height(25.dp))

            // Sección: Ajustes de cuenta
            SeccionNOpciones(
                titulo = "Ajustes de cuenta",
                opciones = listOf(
                    OpcionData(R.drawable.lock, "Cambiar Contraseña"),
                    OpcionData(R.drawable.user, "Editar datos personales"),
                    OpcionData(R.drawable.logout, "Cerrar Sesión") { mostrarDialogo = true }
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Sección: Más información
            SeccionNOpciones(
                titulo = "Más información",
                opciones = listOf(
                    OpcionData(R.drawable.help, "Ayuda"),
                    OpcionData(R.drawable.info, "Acerca de Dirección de Juventud")
                )
            )
        }

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                confirmButton = {}, // sin botones por defecto
                title = null,
                text = {
                    // Fondo del cuadro principal
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White, // fondo blanco
                        shadowElevation = 8.dp, // ligera sombra
                        tonalElevation = 0.dp // sin color del tema
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 24.dp, horizontal = 28.dp)
                        ) {
                            Text(
                                text = "¿Quieres cerrar la sesión?",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Botón "No"
                                Button(
                                    onClick = { mostrarDialogo = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(40.dp),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        width = 2.dp,
                                        brush = SolidColor(moradoTexto)
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(0.dp)
                                ) {
                                    Text(
                                        text = "No",
                                        color = moradoTexto,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.width(25.dp))

                                // Botón "Sí"
                                Button(
                                    onClick = {
                                        mostrarDialogo = false
                                        navController.navigate(Pantalla.RUTA_JN_APP) // tu acción aquí
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = moradoBoton
                                    ),
                                    shape = RoundedCornerShape(40.dp),
                                    elevation = ButtonDefaults.buttonElevation(0.dp)
                                ) {
                                    Text(
                                        text = "Sí",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                },
                // Fondo transparente y sin padding del AlertDialog
                containerColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
fun SeccionNOpciones(titulo: String, opciones: List<OpcionData>) {
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
fun PerfilNegocioPagePreview() {
    val navController = rememberNavController()
    PerfilNegocioPage(navController)
}