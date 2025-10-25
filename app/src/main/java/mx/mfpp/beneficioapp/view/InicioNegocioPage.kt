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
import mx.mfpp.beneficioapp.viewmodel.VerDatosNegocioViewModel

/**
 * Pantalla principal para negocios.
 *
 * Muestra información del negocio, permite escanear códigos QR, agregar nuevas promociones
 * y visualizar las promociones recientes.
 *
 * Funcionalidades:
 * - Mostrar la foto y nombre del negocio.
 * - Navegar a perfil y notificaciones.
 * - Botón para escanear QR.
 * - Botón para agregar nuevas promociones.
 * - Listado de promociones recientes con opción de editar.
 *
 * @param navController Controlador de navegación para moverse entre pantallas.
 */
@Composable
fun InicioNegocioPage(navController: NavController) {
    val moradoBoton = Color(0xFFE2C8FF)
    val moradoTexto = Color(0xFF9605F7)
    val scannerViewModel: ScannerViewModel = viewModel()
    val promocionesViewModel: PromocionesViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val promociones by promocionesViewModel.promociones.collectAsState()
    val vmDatosNegocio: VerDatosNegocioViewModel = viewModel()
    val negocio = vmDatosNegocio.negocio.collectAsState()

    LaunchedEffect(Unit) {
        vmDatosNegocio.cargarDatos(context)
        val idNegocio = sessionManager.getNegocioId() ?: 0
        promocionesViewModel.cargarPromociones(idNegocio)
    }

    val nombreNegocio = negocio.value?.nombre ?: sessionManager.getNombreNegocio() ?: "Negocio"

    Scaffold(containerColor = Color.White) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                            if (negocio.value?.foto?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = negocio.value!!.foto,
                                    contentDescription = "Foto del negocio",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(50.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.size(60.dp),
                                    tint = Color.LightGray
                                )
                            }
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
                Button(
                    onClick = {
                        scannerViewModel.resetScannerState()
                        navController.navigate(Pantalla.RUTA_SCANER_NEGOCIO) { launchSingleTop = true }
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
                            .clickable { navController.navigate("editarPromocion/${promo.id}") }
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

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
/**
 * Preview de la pantalla principal de negocio.
 *
 * Permite visualizar cómo se verá la UI de InicioNegocioPage
 * en el editor de Compose sin necesidad de ejecutar la app en un dispositivo.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioNegocioPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        InicioNegocioPage(navController)
    }
}
