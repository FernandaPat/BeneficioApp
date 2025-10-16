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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.model.Categoria
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.viewmodel.BeneficioJovenVM

/**
 * Pantalla de inicio principal para usuarios jóvenes.
 *
 * Muestra categorías, promociones favoritas, nuevas promociones y promociones cercanas
 * en un diseño scrollable organizado por secciones.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas
 * @param viewModel ViewModel que gestiona el estado y datos de la pantalla
 * @param modifier Modificador de Composable para personalizar el layout
 */
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
        topBar = { HomeTopBar(navController) }
    ) { paddingValues ->

        // Scrollable Column sin mostrar barra
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState(),
                    enabled = true)

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
                        onItemClick = { navController.navigate(Pantalla.RUTA_NEGOCIODETALLE_APP) }
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

/**
 * Componente que muestra un estado de carga.
 *
 * Se muestra mientras se cargan los datos de la aplicación.
 */
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

/**
 * Componente que muestra un estado de error.
 *
 * Se muestra cuando ocurre un error al cargar los datos.
 *
 * @param mensajeError Mensaje descriptivo del error ocurrido
 * @param onReintentar Callback invocado cuando el usuario presiona el botón de reintentar
 */
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

/**
 * Componente que muestra una sección horizontal de items.
 *
 * Presenta una lista de promociones en un scroll horizontal con un título.
 *
 * @param titulo Título descriptivo de la sección
 * @param items Lista de promociones a mostrar en la sección
 * @param onItemClick Callback invocado cuando se hace clic en un item
 * @param modifier Modificador de Composable para personalizar el layout
 */
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

/**
 * Componente que representa un item individual en una sección horizontal.
 *
 * Muestra una promoción con imagen, tiempo de expiración y nombre.
 *
 * @param promocion Datos de la promoción a mostrar
 * @param onItemClick Callback invocado cuando se hace clic en el card
 */
@Composable
fun CardItemHorizontal(promocion: Promocion, onItemClick: () -> Unit) {
    val colorMorado = Color(0xFF6A5ACD) // Color morado estándar - ajusta este valor según tu app

    Card(
        onClick = onItemClick,
        modifier = Modifier
            .size(width = 176.dp, height = 108.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Imagen placeholder (manteniendo el tamaño original)
            AsyncImage(
                model = promocion.imagenUrl ?: "https://picsum.photos/200/300?random=${promocion.id}",
                contentDescription = "Imagen de ${promocion.nombre}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Tiempo de expiración arriba a la derecha (como estaba antes)
            Text(
                text = promocion.obtenerTextoExpiracion(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
            )

            // Fondo morado solo en la parte inferior para el texto
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(colorMorado.copy(alpha = 0.8f))
            )

            // Nombre del lugar abajo - texto más pequeño
            Text(
                text = promocion.nombre,
                color = Color.White,
                fontWeight = FontWeight.Medium, // Cambiado a Medium para que sea un poco menos grueso
                fontSize = 12.sp, // Reducido de 14.sp a 12.sp
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .fillMaxWidth()
            )
        }
    }
}

/**
 * Componente que muestra la sección de categorías.
 *
 * Presenta las categorías disponibles en una cuadrícula de 2 filas (4+3 items).
 *
 * @param categorias Lista de categorías a mostrar
 */
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

/**
 * Componente que representa un item individual de categoría en forma circular.
 *
 * Muestra el icono de la categoría en un card circular.
 *
 * @param categoria Datos de la categoría a mostrar
 */
@Composable
fun ItemCategoriaCirculo(categoria: Categoria) {
    Card(
        onClick = { /* Acción al hacer clic en la categoría */ },
        modifier = Modifier.size(70.dp), // MANTENIDO en 70.dp
        shape = RoundedCornerShape(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = categoria.iconoResId),
                contentDescription = categoria.nombre,
                modifier = Modifier.size(42.dp), // AUMENTADO de 32.dp a 42.dp
                tint = Color.Unspecified
            )
        }
    }
}

/**
 * Barra superior personalizada para la pantalla de inicio.
 *
 * Muestra el perfil del usuario y acceso a notificaciones.
 *
 * @param navController Controlador de navegación para manejar la navegación
 * @param modifier Modificador de Composable para personalizar el layout
 */
@Composable
fun HomeTopBar(navController: NavController, modifier: Modifier = Modifier) {
    // Vamos a hacerla similar a la de negocio pero manteniendo la estructura original
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp) // Reducido de 120.dp para que sea similar a negocio
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 25.dp), // Mismo padding horizontal que negocio
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        navController.navigate(Pantalla.RUTA_PERFIL_APP)
                    },
                    modifier = Modifier.size(60.dp) // Reducido de 90.dp a 60.dp (igual que negocio)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(60.dp), // Reducido de 93.dp a 60.dp
                        tint = Color.LightGray
                    )
                }
                Spacer(modifier = Modifier.width(10.dp)) // Mismo espaciado que negocio
                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.titleMedium.copy( // Mismo estilo que negocio
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }

            IconButton(
                onClick = {
                    navController.navigate(Pantalla.RUTA_NOTIFICACIONES_APP)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bell),
                    contentDescription = "Notificaciones",
                    tint = Color.Gray // Mismo color que negocio
                )
            }
        }
    }
}

/**
 * Previsualización de la pantalla de inicio.
 */
@Preview(showBackground = true)
@Composable
fun InicioPagePreview() {
    MaterialTheme {
        val navController = rememberNavController()
        InicioPage(navController)
    }
}