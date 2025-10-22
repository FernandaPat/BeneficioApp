package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.viewmodel.PromocionesViewModel
import mx.mfpp.beneficioapp.viewmodel.ScannerViewModel
import mx.mfpp.beneficioapp.model.SessionManager

@Composable
fun InicioNegocioPage(navController: NavController) {
    // 🎨 Paleta de colores
    val moradoBoton = Color(0xFFE2C8FF)
    val moradoTexto = Color(0xFF9605F7)

    // 🧠 ViewModels y SessionManager
    val scannerViewModel: ScannerViewModel = viewModel()
    val promocionesViewModel: PromocionesViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // 🧩 Estado de promociones
    val promociones by promocionesViewModel.promociones.collectAsState()

    // 🧩 Cargar promociones con ID dinámico
    LaunchedEffect(Unit) {
        val idNegocio = sessionManager.getNegocioId() ?: 0
        promocionesViewModel.cargarPromociones(idNegocio)
    }

    // 🧾 Nombre del negocio dinámico
    val nombreNegocio = sessionManager.getNombreNegocio() ?: "Negocio"

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 ENCABEZADO
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { navController.navigate(Pantalla.RUTA_PERFIL_NEGOCIO) },
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.size(60.dp),
                                tint = Color.LightGray
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = nombreNegocio,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }

                    IconButton(onClick = {
                        navController.navigate(Pantalla.RUTA_NOTIFICACIONES_NEGOCIO)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.bell),
                            contentDescription = "Notificaciones",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 🔹 BOTÓN ESCANEAR QR
                Button(
                    onClick = {
                        scannerViewModel.resetScannerState()
                        navController.navigate(Pantalla.RUTA_SCANER_NEGOCIO) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = moradoBoton),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Escanear QR",
                        tint = moradoTexto
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Escanear QR",
                        color = moradoTexto,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // 🔹 BOTÓN AGREGAR PROMOCIÓN
                Button(
                    onClick = { navController.navigate(Pantalla.RUTA_AGREGAR_PROMOCIONES) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = moradoBoton),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.gift),
                        contentDescription = "Agregar promoción",
                        tint = moradoTexto
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Agregar Promoción",
                        color = moradoTexto,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                // 🔹 TÍTULO SECCIÓN
                Text(
                    text = "Promociones recientes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 🔹 LISTA DE PROMOCIONES
            if (promociones.isEmpty()) {
                item {
                    Text(
                        text = "No hay promociones registradas aún.",
                        modifier = Modifier.padding(25.dp),
                        color = Color.Gray
                    )
                }
            } else {
                items(promociones) { promo ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 10.dp)
                            .clickable {
                                println("🟣 Navegando a editarPromocion/${promo.id}")
                                navController.navigate("editarPromocion/${promo.id}")
                            }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(promo.imagenUrl ?: "https://picsum.photos/300")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Promoción",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = promo.nombre,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Text(
                            text = promo.descripcion ?: "Sin descripción",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // 🔹 ESPACIADO FINAL PARA LA BARRA INFERIOR
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioNegocioPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        InicioNegocioPage(navController)
    }
}
