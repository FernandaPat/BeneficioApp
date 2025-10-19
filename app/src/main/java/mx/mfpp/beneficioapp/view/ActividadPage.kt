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
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.viewmodel.PromocionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadPage(
    navController: NavController,
    promocionesViewModel: PromocionesViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Estados observados desde el ViewModel
    val promociones by promocionesViewModel.promociones.collectAsState()
    val isLoading by promocionesViewModel.isLoading.collectAsState()
    val error by promocionesViewModel.error.collectAsState()

    // 🔹 Cargar promociones del negocio al abrir la pantalla
    LaunchedEffect(Unit) {
        val idNegocio = sessionManager.getNegocioId() ?: 0
        if (idNegocio != 0) {
            promocionesViewModel.cargarPromociones(idNegocio)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Promociones registradas",
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
                        color = Color(0xFF9605F7)
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

                promociones.isEmpty() -> {
                    Text(
                        text = "No hay promociones registradas para este negocio.",
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
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        items(promociones, key = { it.id }) { promo ->
                            PromocionListItem(
                                promo = promo,
                                onEditClick = {
                                    // 🔹 Navegar al editor con el ID de la promoción
                                    navController.navigate("editarPromocion/${promo.id}")
                                }
                            )
                            HorizontalDivider(
                                color = Color(0xFFF3F3F3),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Elemento individual del listado de promociones
 */
@Composable
private fun PromocionListItem(
    promo: Promocion,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 🖼️ Imagen + texto principal
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF7F7F7)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = promo.imagenUrl ?: "https://picsum.photos/200",
                    contentDescription = promo.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = promo.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = promo.descripcion ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                )
            }
        }

        // 📅 Fecha de expiración o botón editar
        TextButton(onClick = onEditClick) {
            Text(
                text = "Editar",
                color = Color(0xFF9605F7),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
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
