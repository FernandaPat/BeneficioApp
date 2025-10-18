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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.model.PromocionJoven
import mx.mfpp.beneficioapp.viewmodel.BusquedaViewModel
import mx.mfpp.beneficioapp.viewmodel.NegocioDetalleViewModel

@Composable
fun NegocioDetallePage(
    id: String?,
    navController: NavController,
    busquedaViewModel: BusquedaViewModel = viewModel()
) {
    // Obtener el establecimiento de la lista de establecimientos
    val establecimientos by busquedaViewModel.establecimientos.collectAsState()
    val establecimientoId = id?.toIntOrNull() ?: 0

    // Buscar el establecimiento por ID
    val establecimiento = remember(establecimientos, establecimientoId) {
        establecimientos.find { it.id_establecimiento == establecimientoId }
    }

    // Si no encontramos el establecimiento, mostrar error
    if (establecimiento == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Establecimiento no encontrado",
                color = Color.Red,
                fontSize = 18.sp
            )
        }
        return
    }

    // Usar el ViewModel para cargar las promociones
    val viewModel: NegocioDetalleViewModel = viewModel(
        factory = NegocioDetalleViewModel.NegocioDetalleViewModelFactory(establecimientoId)
    )

    val promociones by viewModel.promociones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val moradoBoton = Color(0xFFE2C8FF)
    val moradoTexto = Color(0xFF9605F7)

    LaunchedEffect(establecimientoId) {
        viewModel.cargarDatos()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AsyncImage(
                    model = establecimiento.foto ?: "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267",
                    contentDescription = "Imagen del negocio",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = establecimiento.nombre,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${establecimiento.nombre_categoria} • ${establecimiento.colonia}",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { navController.navigate(Pantalla.RUTA_MAPA_APP) },
                    colors = ButtonDefaults.buttonColors(containerColor = moradoBoton),
                    shape = RoundedCornerShape(50.dp),
                    contentPadding = PaddingValues(horizontal = 25.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Ver en el mapa",
                        color = moradoTexto,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "Promociones activas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (isLoading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(20.dp)
                            .size(48.dp),
                        color = moradoTexto
                    )
                }
            } else if (error != null) {
                item {
                    Text(
                        text = error ?: "Error desconocido",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (promociones.isEmpty()) {
                item {
                    Text(
                        text = "No hay promociones activas en este momento",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(promociones) { promocion ->
                    PromocionItem(
                        promocion = promocion,
                        onAplicarClick = {
                            // Acción al aplicar la promoción (por ejemplo, navegar a QR)
                            navController.navigate(Pantalla.RUTA_QR_PROMOCION)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun PromocionItem(
    promocion: PromocionJoven,
    onAplicarClick: () -> Unit
) {
    val moradoBoton = Color(0xFFE2C8FF)
    val moradoTexto = Color(0xFF9605F7)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp)
            .heightIn(min = 90.dp), // Altura mínima consistente
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = promocion.foto ?: "https://via.placeholder.com/150",
            contentDescription = promocion.titulo_promocion,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp) // Espacio entre texto y botón
        ) {
            Text(
                text = promocion.titulo_promocion,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                lineHeight = 18.sp // Reducir interlineado
            )
            Spacer(modifier = Modifier.height(2.dp)) // Menos espacio entre título y descripción
            Text(
                text = promocion.descripcion,
                fontSize = 15.sp,
                color = Color(0xFF8B8B8B),
                lineHeight = 16.sp, // Reducir interlineado
                maxLines = 2 // Limitar a 2 líneas máximo
            )
            Spacer(modifier = Modifier.height(2.dp)) // Menos espacio entre descripción y fecha
            Text(
                text = "Válido hasta: ${promocion.fecha_expiracion}",
                fontSize = 12.sp,
                color = Color(0xFF666666),
                lineHeight = 14.sp // Reducir interlineado
            )
        }

        Button(
            onClick = onAplicarClick,
            colors = ButtonDefaults.buttonColors(containerColor = moradoBoton),
            shape = RoundedCornerShape(40.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Aplicar",
                color = moradoTexto,
                fontWeight = FontWeight.Medium
            )
        }
    }
}