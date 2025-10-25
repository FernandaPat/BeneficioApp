package mx.mfpp.beneficioapp.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.viewmodel.PerfilViewModel
import mx.mfpp.beneficioapp.viewmodel.SubirFotoJovenViewModel
import mx.mfpp.beneficioapp.viewmodel.VerDatosPersonalesViewModel
/**
 * Pantalla de perfil del usuario donde puede ver y editar su información personal.
 *
 * Funcionalidades principales:
 * - Muestra la foto de perfil del joven, con opción de cambiarla.
 * - Permite subir una nueva foto y actualizarla en el servidor.
 * - Muestra el nombre del usuario y carga los datos desde la ViewModel correspondiente.
 * - Secciones de ajustes de cuenta: cambiar contraseña, ver datos personales y cerrar sesión.
 * - Secciones de más información: ayuda y acerca de la Dirección de Juventud.
 * - Diálogo de confirmación al cerrar sesión.
 * - Botón flotante para guardar la nueva foto seleccionada.
 *
 * @param navController Controlador de navegación para moverse a otras pantallas.
 */
@Composable
fun PerfilPage(navController: NavController) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    val viewModel: PerfilViewModel = viewModel()
    val vmSubirFoto: SubirFotoJovenViewModel = viewModel()
    val vmDatos: VerDatosPersonalesViewModel = viewModel()

    val joven = vmDatos.joven.collectAsState()
    val cargando = vmDatos.cargando.collectAsState()
    val error = vmDatos.error.collectAsState()
    val context = LocalContext.current

    // 🔄 Cargar datos iniciales del joven
    LaunchedEffect(Unit) {
        vmDatos.cargarDatos(context)
        viewModel.logoutEvent.collect {
            navController.navigate(Pantalla.RUTA_JN_APP) {
                popUpTo(0)
            }
        }
    }

    val moradoClaro = Color(0xFFE9D4FF)
    val moradoTexto = Color(0xFF9605F7)
    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
        uri = result
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // === ENCABEZADO ===
            Box(
                modifier = Modifier.fillMaxWidth().background(moradoClaro).height(180.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Regresar",
                        modifier = Modifier.size(30.dp),
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
                    // Fondo circular
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                    )

                    // Mostrar imagen (1. Nueva, 2. API, 3. Placeholder)
                    when {
                        uri != null -> {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Nueva imagen seleccionada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize().clip(CircleShape)
                            )
                        }
                        joven.value?.foto?.isNotBlank() == true -> {
                            AsyncImage(
                                model = joven.value!!.foto,
                                contentDescription = "Foto actual del joven",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize().clip(CircleShape)
                            )
                        }
                        else -> {
                            IconButton(
                                onClick = { pickImage.launch("image/*") },
                                modifier = Modifier.size(160.dp).clip(CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Seleccionar imagen",
                                    tint = moradoTexto
                                )
                            }
                        }
                    }

                    // 🟣 Botón flotante editar
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

            Spacer(Modifier.height(100.dp))
            when {
                cargando.value -> CircularProgressIndicator(color = moradoTexto)
                error.value != null -> Text("⚠️ ${error.value}", color = Color.Red, fontSize = 16.sp)
                joven.value != null -> Text(
                    text = joven.value!!.nombre,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.height(25.dp))

            // === SECCIÓN AJUSTES DE CUENTA ===
            SeccionOpciones(
                titulo = "Ajustes de cuenta",
                opciones = listOf(
                    OpcionData(R.drawable.lock, "Cambiar Contraseña") {
                        navController.navigate(Pantalla.RUTA_RECUPERAR_CONTRASENA)
                    },
                    OpcionData(R.drawable.user, "Ver datos personales") {
                        navController.navigate(Pantalla.RUTA_DATOSPERSONALES_APP)
                    },
                    OpcionData(R.drawable.logout, "Cerrar Sesión") { mostrarDialogo = true }
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            // === SECCIÓN MÁS INFORMACIÓN ===
            SeccionOpciones(
                titulo = "Más información",
                opciones = listOf(
                    OpcionData(R.drawable.help, "Ayuda") {
                        navController.navigate(Pantalla.RUTA_AYUDA_APP)
                    },
                    OpcionData(R.drawable.info, "Acerca de Dirección de Juventud") {
                        navController.navigate(Pantalla.RUTA_ACERCADE_APP)
                    }
                )
            )
        }

        // === DIÁLOGO CIERRE DE SESIÓN ===
        if (mostrarDialogo) {
            Dialog(onDismissRequest = { mostrarDialogo = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    tonalElevation = 2.dp,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 280.dp, max = 360.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(moradoTexto.copy(alpha = 0.25f))
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "¿Quieres cerrar la sesión?",
                            color = Color.Black,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(24.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                16.dp,
                                Alignment.CenterHorizontally
                            )
                        ) {
                            OutlinedButton(
                                onClick = { mostrarDialogo = false },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = moradoTexto
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("No")
                            }

                            Button(
                                onClick = {
                                    mostrarDialogo = false
                                    viewModel.cerrarSesion()
                                },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = moradoTexto.copy(alpha = 0.25f),
                                    contentColor = moradoTexto
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sí")
                            }
                        }
                    }
                }
            }
        }

        // === BOTÓN FLOTANTE GUARDAR FOTO ===
        val mensaje by vmSubirFoto.mensaje.collectAsState()
        val subiendo by vmSubirFoto.subiendo.collectAsState()

        mensaje?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                vmSubirFoto.limpiarMensaje()
                vmDatos.cargarDatos(context) // 🔄 refresca datos después de subir
                uri = null
            }
        }

        if (uri != null && !subiendo) {
            FloatingActionButton(
                onClick = { vmSubirFoto.subirFoto(uri!!) },
                containerColor = moradoTexto,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = "Guardar foto")
            }
        }

        if (subiendo) {
            CircularProgressIndicator(
                color = moradoTexto,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
            )
        }
    }
}

/**
 * Clase de datos que representa una opción individual en las secciones de configuración.
 *
 * @property icono Resource ID del icono que representa la opción
 * @property texto Texto descriptivo de la opción
 * @property onClick Función callback opcional que se ejecuta al hacer clic en la opción
 */
data class OpcionData(
    val icono: Int,
    val texto: String,
    val onClick: (() -> Unit)? = null
)
/**
 * Composable reutilizable que muestra una sección con título y lista de opciones.
 *
 * Cada sección se presenta como una tarjeta elevada con opciones que incluyen
 * iconos, texto y flechas indicadoras. Las opciones están separadas por divisores
 * y son clickeables si tienen una acción asociada.
 *
 * @param titulo Texto que identifica la sección de opciones
 * @param opciones Lista de [OpcionData] que representan las opciones disponibles en la sección
 */
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
/**
 * Función de preview para visualizar el diseño de la pantalla de perfil de usuario en Android Studio.
 *
 * Permite ver la interfaz completa con el encabezado morado, imagen de perfil y secciones
 * de opciones en un contexto aislado durante el desarrollo.
 *
 * @see Preview Anotación que habilita la visualización en el panel de diseño de Android Studio
 */

@Preview(showBackground = true)
@Composable
fun PerfilPagePreview() {
    val navController = rememberNavController()
    PerfilPage(navController)
}