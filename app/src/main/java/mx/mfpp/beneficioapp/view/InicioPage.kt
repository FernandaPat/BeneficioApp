package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.model.Categoria
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.viewmodel.BeneficioJovenVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioPage(
    navController: NavController,
    viewModel: BeneficioJovenVM = viewModel(),
    modifier: Modifier = Modifier
) {
    val categorias by viewModel.categorias.collectAsState()
    val favoritos by viewModel.favoritos.collectAsState()
    val nuevasPromociones by viewModel.nuevasPromociones.collectAsState()
    val promocionesExpiracion by viewModel.promocionesExpiracion.collectAsState()
    val promocionesCercanas by viewModel.promocionesCercanas.collectAsState()
    val estadoCargando by viewModel.estadoCargando.collectAsState()
    val error by viewModel.error.collectAsState()


    Scaffold(
        topBar = { HomeTopBar(navController) },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                estadoCargando -> {
                    EstadoCargando()
                }
                error != null -> {
                    EstadoError(
                        mensajeError = error!!,
                        onReintentar = { viewModel.refrescarDatos() }
                    )
                }
                else -> {
                    Categorias(categorias = categorias)
                    SeccionHorizontal(
                        titulo = "Tus Favoritos",
                        items = favoritos,
                        onItemClick = { /* Navegar a detalle */ }
                    )
                    SeccionHorizontal(
                        titulo = "Nuevas Promociones",
                        items = nuevasPromociones,
                        onItemClick = { /* Navegar a detalle */ }
                    )
                    SeccionHorizontal(
                        titulo = "Expiran pronto",
                        items = promocionesExpiracion,
                        onItemClick = { /* Navegar a detalle */ }
                    )
                    SeccionHorizontal(
                        titulo = "Cerca de ti",
                        items = promocionesCercanas,
                        onItemClick = { /* Navegar a detalle */ }
                    )
                }
            }
        }
    }
}

@Composable
fun EstadoCargando() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = "Cargando promociones...",
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun EstadoError(mensajeError: String, onReintentar: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Error",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Text(
                text = mensajeError,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = onReintentar,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
fun SeccionHorizontal(
    titulo: String,
    items: List<Promocion>,
    onItemClick: (Promocion) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                CardItemHorizontal(promocion = item, onItemClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
fun CardItemHorizontal(promocion: Promocion, onItemClick: () -> Unit) {
    Card(
        onClick = onItemClick,
        modifier = Modifier
            .size(width = 176.dp, height = 108.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = promocion.imagenUrl ?: "https://picsum.photos/200/300?random=${promocion.id}",
                contentDescription = "Imagen de ${promocion.nombre}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = promocion.obtenerRatingTexto(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = promocion.obtenerTextoExpiracion(),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }

                Column {
                    Text(
                        text = promocion.nombre,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    promocion.descuento?.let { descuento ->
                        Text(
                            text = descuento,
                            color = Color.Yellow,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Categorias(categorias: List<Categoria>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Categorías",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val primeras4Categorias = categorias.take(4)
        val ultimas3Categorias = categorias.takeLast(3)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Start,
        ) {
            primeras4Categorias.forEachIndexed { index, categoria ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(end = if (index < 3) 12.dp else 0.dp)
                ) {
                    ItemCategoriaCirculo(categoria = categoria)
                    Text(
                        text = categoria.nombre,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .width(80.dp),
                        color = Color.Black
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            ultimas3Categorias.forEachIndexed { index, categoria ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(end = if (index < 2) 12.dp else 0.dp)
                ) {
                    ItemCategoriaCirculo(categoria = categoria)
                    Text(
                        text = categoria.nombre,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .width(80.dp),
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCategoriaCirculo(categoria: Categoria) {
    Card(
        onClick = { /* Acción al hacer clic en la categoría */ },
        modifier = Modifier.size(70.dp),
        shape = RoundedCornerShape(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = categoria.icono,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun HomeTopBar(navController: NavController, modifier: Modifier = Modifier) {
    val barHeight = 120.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .padding(bottom = 16.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(93.dp)
                        .padding(end = 12.dp),
                    tint = Color.LightGray
                )
                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Color.Black
                    )
                )
            }

            IconButton(
                onClick = {
                    // navController.navigate(Pantalla.RUTA_NOTIFICACIONES_APP)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InicioPagePreview() {
    MaterialTheme {
        val navController = rememberNavController()
        InicioPage(navController)
    }
}

