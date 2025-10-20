package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.model.HistorialPromocionUsuario
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.viewmodel.HistorialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadPage(
    navController: NavController,
    historialViewModel: HistorialViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Estados observados desde el ViewModel de historial
    val historial by historialViewModel.historial.collectAsState()
    val isLoading by historialViewModel.isLoading.collectAsState()
    val error by historialViewModel.error.collectAsState()

    // 🔹 Cargar historial del usuario al abrir la pantalla
    LaunchedEffect(Unit) {
        val idUsuario = sessionManager.getJovenId() ?: 0
        if (idUsuario != 0) {
            historialViewModel.cargarHistorialUsuario(idUsuario)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Promociones registradas", // 🔹 TÍTULO EXACTAMENTE IGUAL
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF9605F7) // 🔹 COLOR EXACTAMENTE IGUAL
                    )
                }

                error != null -> {
                    Text(
                        text = error ?: "Error desconocido",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 20.dp)
                    )
                }

                historial.isEmpty() -> {
                    Text(
                        text = "No hay promociones registradas para este negocio.", // 🔹 TEXTO EXACTAMENTE IGUAL
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 20.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 10.dp) // 🔹 PADDING EXACTAMENTE IGUAL
                    ) {
                        items(historial, key = { it.id }) { itemHistorial ->
                            HistorialListItem(
                                historialItem = itemHistorial,
                                onItemClick = {
                                    // 🔹 Navegar al detalle del establecimiento
                                    navController.navigate("${Pantalla.RUTA_NEGOCIODETALLE_APP}/${itemHistorial.id_establecimiento}")
                                }
                            )
                            HorizontalDivider(
                                color = Color(0xFFF3F3F3), // 🔹 COLOR EXACTAMENTE IGUAL
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 20.dp) // 🔹 PADDING EXACTAMENTE IGUAL
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Elemento individual del listado - DISEÑO IDÉNTICO AL ORIGINAL
 */
@Composable
private fun HistorialListItem(
    historialItem: HistorialPromocionUsuario,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp), // 🔹 PADDING EXACTAMENTE IGUAL
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 🖼️ Imagen + texto principal - ESTRUCTURA IDÉNTICA
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(65.dp) // 🔹 TAMAÑO EXACTAMENTE IGUAL
                    .clip(RoundedCornerShape(10.dp)) // 🔹 BORDER RADIUS EXACTAMENTE IGUAL
                    .background(Color(0xFFF7F7F7)), // 🔹 COLOR EXACTAMENTE IGUAL
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = historialItem.foto ?: "https://picsum.photos/200",
                    contentDescription = historialItem.titulo_promocion,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp)) { // 🔹 PADDING EXACTAMENTE IGUAL
                Text(
                    text = historialItem.titulo_promocion, // 🔹 SOLO CAMBIO DE DATOS
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black, // 🔹 COLOR EXACTAMENTE IGUAL
                        fontSize = 16.sp, // 🔹 TAMAÑO EXACTAMENTE IGUAL
                        fontWeight = FontWeight.Bold // 🔹 FONT WEIGHT EXACTAMENTE IGUAL
                    )
                )
                Text(
                    text = historialItem.descripcion ?: "", // 🔹 SOLO CAMBIO DE DATOS
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray, // 🔹 COLOR EXACTAMENTE IGUAL
                        fontSize = 14.sp // 🔹 TAMAÑO EXACTAMENTE IGUAL
                    )
                )
            }
        }

        // 📅 Mostrar fecha de uso en lugar del botón "Editar"
        Text(
            text = formatearFechaCorta(historialItem.fecha_uso),
            color = Color(0xFF9605F7), // 🔹 COLOR EXACTAMENTE IGUAL AL BOTÓN "Editar"
            fontSize = 14.sp, // 🔹 TAMAÑO EXACTAMENTE IGUAL
            fontWeight = FontWeight.Medium // 🔹 FONT WEIGHT EXACTAMENTE IGUAL
        )
    }
}

/**
 * Función auxiliar para formatear la fecha de manera corta
 */
private fun formatearFechaCorta(fecha: String): String {
    return try {
        // Formato: "2025-09-22T00:00:00" -> "22/09/25"
        val partes = fecha.split("T")[0].split("-")
        if (partes.size == 3) {
            "${partes[2]}/${partes[1]}/${partes[0].takeLast(2)}"
        } else {
            fecha
        }
    } catch (e: Exception) {
        fecha
    }
}

/**
 * Vista previa
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActividadRecientePreview() {
    MaterialTheme {
        val navController = rememberNavController()
        ActividadPage(navController)
    }
}