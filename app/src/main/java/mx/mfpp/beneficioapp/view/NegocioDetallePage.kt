package mx.mfpp.beneficioapp.view

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.model.PromocionJoven
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.viewmodel.BusquedaViewModel
import mx.mfpp.beneficioapp.viewmodel.FavoritosViewModel
import mx.mfpp.beneficioapp.viewmodel.NegocioDetalleViewModel

@Composable
fun NegocioDetallePage(
    id: String?,
    navController: NavController,
    busquedaViewModel: BusquedaViewModel = viewModel()
) {
    val establecimientos by busquedaViewModel.establecimientos.collectAsState()
    val establecimientoId = id?.toIntOrNull() ?: 0
    val context = LocalContext.current

    // 🔹 NUEVO: Recargar favoritos al entrar a la pantalla
    LaunchedEffect(establecimientoId) {
        busquedaViewModel.recargarFavoritos(context) // Pasar context explícitamente
    }

    // 🔹 MEJORADO: Usar derivedStateOf para reactividad
    val establecimientoOriginal by remember(establecimientos, establecimientoId) {
        derivedStateOf {
            establecimientos.find { it.id_establecimiento == establecimientoId }
        }
    }

    // 🔹 CORRECCIÓN: Estado local sincronizado con el ViewModel
    var esFavorito by remember(establecimientoOriginal) {
        mutableStateOf(establecimientoOriginal?.es_favorito ?: false)
    }

    // 🔹 ACTUALIZAR cuando cambie el establecimiento original
    LaunchedEffect(establecimientoOriginal) {
        establecimientoOriginal?.let {
            esFavorito = it.es_favorito
            Log.d("NEGOCIO_DETALLE", "🔄 Establecimiento actualizado: ${it.nombre} - Favorito: ${it.es_favorito}")
        }
    }

    if (establecimientoOriginal == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Establecimiento no encontrado", color = Color.Red, fontSize = 18.sp)
                Button(
                    onClick = { busquedaViewModel.recargarFavoritos(context) }, // Pasar context
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Reintentar carga")
                }
            }
        }
        return
    }

    val sessionManager = remember { SessionManager(context) }
    val favoritosViewModel = remember { FavoritosViewModel(sessionManager) }
    val mensajeFavoritos by favoritosViewModel.mensaje.collectAsState()

    LaunchedEffect(mensajeFavoritos) {
        mensajeFavoritos?.let { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            favoritosViewModel.clearMensaje()

            // 🔹 NUEVO: Recargar favoritos después de cualquier operación
            if (mensaje.contains("Error") || mensaje.contains("ya está") ||
                mensaje.contains("Agregado") || mensaje.contains("Eliminado")) {
                busquedaViewModel.recargarFavoritos(context) // Pasar context
            }
        }
    }

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

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                    AsyncImage(
                        model = establecimientoOriginal!!.foto ?: "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267",
                        contentDescription = "Imagen del negocio",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = {
                            Log.d("NEGOCIO_DETALLE", "❤️ Toggle favorito: $establecimientoId - Estado actual: $esFavorito")
                            favoritosViewModel.toggleFavorito(
                                idEstablecimiento = establecimientoOriginal!!.id_establecimiento,
                                esFavoritoActual = esFavorito,
                                busquedaViewModel = busquedaViewModel,
                                onUpdateDetalle = { nuevoEstado ->
                                    // 🔹 CORRECCIÓN: Actualizar estado local inmediatamente
                                    esFavorito = nuevoEstado
                                    Log.d("NEGOCIO_DETALLE", "✅ Estado actualizado localmente: $nuevoEstado")
                                }
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(54.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (esFavorito) Color.Red else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = establecimientoOriginal!!.nombre,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${establecimientoOriginal!!.nombre_categoria} • ${establecimientoOriginal!!.colonia}",
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
                    Text("Ver en el mapa", color = moradoTexto, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    "Promociones activas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            when {
                isLoading -> item {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(20.dp).size(48.dp),
                        color = moradoTexto
                    )
                }
                error != null -> item {
                    Text(
                        error ?: "Error desconocido",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                promociones.isEmpty() -> item {
                    Text(
                        "No hay promociones activas en este momento",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> items(promociones) { promocion ->
                    PromocionItem(
                        promocion = promocion,
                        onAplicarClick = { navController.navigate(Pantalla.RUTA_QR_PROMOCION) }
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
            .heightIn(min = 90.dp),
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
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        ) {
            Text(
                text = promocion.titulo_promocion,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = promocion.descripcion,
                fontSize = 15.sp,
                color = Color(0xFF8B8B8B),
                lineHeight = 16.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Válido hasta: ${promocion.fecha_expiracion}",
                fontSize = 12.sp,
                color = Color(0xFF666666),
                lineHeight = 14.sp
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
