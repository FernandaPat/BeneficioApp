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
import mx.mfpp.beneficioapp.model.PromocionJoven
import mx.mfpp.beneficioapp.viewmodel.CategoriasViewModel
import mx.mfpp.beneficioapp.viewmodel.PromocionJovenViewModel

/**
 * Pantalla de inicio principal para usuarios jóvenes.
 *
 * Muestra categorías, promociones favoritas, nuevas promociones y promociones cercanas
 * en un diseño scrollable organizado por secciones.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas
 * @param categoriasViewModel ViewModel para categorías
 * @param promocionesViewModel ViewModel para promociones
 * @param modifier Modificador de Composable para personalizar el layout
 */
@Composable
fun InicioPage(
    navController: NavController,
    categoriasViewModel: CategoriasViewModel = viewModel(),
    promocionesViewModel: PromocionJovenViewModel = viewModel(), // Cambiar aquí
    modifier: Modifier = Modifier
) {
    val categorias by categoriasViewModel.categorias.collectAsState()
    val categoriasLoading by categoriasViewModel.isLoading.collectAsState()
    val categoriasError by categoriasViewModel.error.collectAsState()

    // SOLO estas dos secciones ahora
    val nuevasPromociones by promocionesViewModel.nuevasPromociones.collectAsState()
    val promocionesExpiracion by promocionesViewModel.promocionesExpiracion.collectAsState()
    val promocionesLoading by promocionesViewModel.isLoading.collectAsState()
    val promocionesError by promocionesViewModel.error.collectAsState()

    val isLoading = categoriasLoading || promocionesLoading
    val error = categoriasError ?: promocionesError

    Scaffold(
        topBar = { HomeTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                isLoading -> {
                    EstadoCargando()
                }
                error != null -> {
                    EstadoError(
                        mensajeError = error,
                        onReintentar = {
                            categoriasViewModel.refrescarCategorias()
                            promocionesViewModel.refrescarPromociones()
                        }
                    )
                }
                else -> {
                    Categorias(categorias = categorias)

                    // SOLO estas dos secciones
                    SeccionHorizontal(
                        titulo = "Nuevas Promociones",
                        items = nuevasPromociones,
                        onItemClick = { promocion ->
                            navController.navigate("${Pantalla.RUTA_NEGOCIODETALLE_APP}/${promocion.id}")
                        }
                    )
                    SeccionHorizontal(
                        titulo = "Expiran pronto",
                        items = promocionesExpiracion,
                        onItemClick = { promocion ->
                            navController.navigate("${Pantalla.RUTA_NEGOCIODETALLE_APP}/${promocion.id}")
                        }
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
    items: List<PromocionJoven>, // Cambiar aquí
    onItemClick: (PromocionJoven) -> Unit, // Cambiar aquí
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

        if (items.isEmpty()) {
            Text(
                text = "No hay $titulo disponibles",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    CardItemHorizontal(promocion = item, onItemClick = { onItemClick(item) })
                }
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
fun CardItemHorizontal(promocion: PromocionJoven, onItemClick: () -> Unit) {
    val colorMorado = Color(0xFF6A5ACD)

    Card(
        onClick = onItemClick,
        modifier = Modifier
            .size(width = 176.dp, height = 108.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Imagen - usa promocion.foto
            AsyncImage(
                model = promocion.foto ?: "https://picsum.photos/200/300?random=${promocion.id}",
                contentDescription = "Imagen de ${promocion.titulo_promocion}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Tiempo de expiración
            Text(
                text = "Válido hasta ${promocion.fecha_expiracion}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
            )

            // Fondo morado para el texto
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(colorMorado.copy(alpha = 0.8f))
            )

            // Nombre de la promoción
            Text(
                text = promocion.titulo_promocion,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
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

        if (categorias.isEmpty()) {
            Text(
                text = "No hay categorías disponibles",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
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
        onClick = {
            // Acción al hacer clic en la categoría - podría navegar a resultados filtrados
        },
        modifier = Modifier.size(70.dp),
        shape = RoundedCornerShape(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = categoria.iconoResId),
                contentDescription = categoria.nombre,
                modifier = Modifier.size(42.dp),
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        navController.navigate(Pantalla.RUTA_PERFIL_APP)
                    },
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
                    text = "Nombre",
                    style = MaterialTheme.typography.titleMedium.copy(
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
                    tint = Color.Gray
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