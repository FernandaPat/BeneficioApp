package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

/**
 * Pantalla principal que muestra el historial de promociones canjeadas por el usuario.
 *
 * Esta función obtiene el ID del usuario desde el [SessionManager],
 * carga su historial de promociones usando el [HistorialViewModel],
 * y muestra diferentes estados de la UI según si está cargando, si hubo un error,
 * o si el historial está vacío.
 *
 * @param navController Controlador de navegación utilizado para moverse entre pantallas.
 * @param historialViewModel ViewModel que gestiona los datos del historial del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadPage(
    navController: NavController,
    historialViewModel: HistorialViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Estado del historial, carga y error desde el ViewModel
    val historial by historialViewModel.historial.collectAsState()
    val isLoading by historialViewModel.isLoading.collectAsState()
    val error by historialViewModel.error.collectAsState()

    /**
     * Efecto lanzado al crear la pantalla.
     * Carga el historial del usuario autenticado, si existe.
     */
    LaunchedEffect(Unit) {
        val idUsuario = sessionManager.getJovenId()
        if (idUsuario != null) {
            historialViewModel.cargarHistorialUsuario(idUsuario)
        } else {
            historialViewModel.clearError()
        }
    }

    // Estructura principal de la pantalla
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Promociones registradas",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                // Estado de carga
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF9605F7)
                    )
                }

                // Estado de error
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

                // Historial vacío
                historial.isEmpty() -> {
                    Text(
                        text = "No hay promociones registradas para este negocio.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 20.dp)
                    )
                }

                // Mostrar lista de historial
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        // Encabezado con conteo de cupones
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Cupones Canjeados (${historial.size})",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Fecha",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Elementos del historial
                        items(historial, key = { it.id }) { itemHistorial ->
                            HistorialListItem(
                                historialItem = itemHistorial,
                                onItemClick = {
                                    navController.navigate("${Pantalla.RUTA_NEGOCIODETALLE_APP}/${itemHistorial.id_establecimiento}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Elemento individual de la lista de historial de promociones.
 *
 * Muestra la información principal de una promoción canjeada, incluyendo
 * imagen, nombre, descripción, establecimiento y fecha de canje.
 *
 * @param historialItem Objeto que representa una promoción canjeada.
 * @param onItemClick Acción ejecutada al hacer clic sobre el elemento.
 * @param modifier Modificador opcional para personalizar la apariencia del contenedor.
 */
@Composable
private fun HistorialListItem(
    historialItem: HistorialPromocionUsuario,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(12.dp)
                .clickable { onItemClick() }, // Se puede agregar interacción
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la promoción
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF7F7F7)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = historialItem.foto_url ?: "https://via.placeholder.com/200",
                    contentDescription = historialItem.titulo_promocion,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información textual de la promoción
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = historialItem.titulo_promocion,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = historialItem.descripcion ?: "",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = historialItem.nombre_establecimiento,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Fecha a la derecha
            Box(
                modifier = Modifier
                    .height(28.dp)
                    .width(80.dp)
                    .background(
                        color = Color(0xFF9605F7).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatearFechaCorta(historialItem.fecha_canje),
                    color = Color(0xFF9605F7),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // Separador entre elementos
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFF3F3F3))
        )
    }
}

/**
 * Formatea una fecha en formato ISO (yyyy-MM-ddTHH:mm:ss)
 * a un formato corto legible (dd/MM/yy).
 *
 * Ejemplo: "2025-10-25T12:00:00" → "25/10/25"
 *
 * @param fecha Cadena con la fecha en formato ISO.
 * @return Fecha formateada o el texto original si ocurre un error.
 */
private fun formatearFechaCorta(fecha: String): String {
    return try {
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
 * Vista previa del composable [ActividadPage] para el modo diseño.
 *
 * Permite visualizar la pantalla en el editor sin necesidad de ejecutar la app.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActividadRecientePreview() {
    MaterialTheme {
        val navController = rememberNavController()
        ActividadPage(navController)
    }
}