package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import mx.mfpp.beneficioapp.model.Categoria
import mx.mfpp.beneficioapp.model.Establecimiento
import mx.mfpp.beneficioapp.viewmodel.BusquedaViewModel
import mx.mfpp.beneficioapp.viewmodel.CategoriasViewModel

/**
 * Pantalla que muestra resultados de búsqueda de establecimientos.
 *
 * Permite filtrar establecimientos por categoría y texto de búsqueda,
 * mostrando los resultados en una lista interactiva.
 *
 * @param navController Controlador de navegación para manejar cambios de pantalla
 * @param categoriaSeleccionada Categoría pre-seleccionada para filtrar (opcional)
 * @param categoriasViewModel ViewModel para categorías
 * @param busquedaViewModel ViewModel para búsqueda
 * @param modifier Modificador de Composable para personalizar el layout
 */
@Composable
fun ResultadosPage(
    navController: NavController,
    categoriaSeleccionada: String? = null,
    categoriasViewModel: CategoriasViewModel = viewModel(),
    busquedaViewModel: BusquedaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val establecimientos by busquedaViewModel.establecimientos.collectAsState()
    val categorias by categoriasViewModel.categorias.collectAsState()
    val searchText by busquedaViewModel.textoBusqueda.collectAsState()
    val categoriaSeleccionadaState by busquedaViewModel.categoriaSeleccionada.collectAsState()

    LaunchedEffect(categoriaSeleccionada) {
        if (categoriaSeleccionada != null) {
            busquedaViewModel.seleccionarCategoria(categoriaSeleccionada)
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            CategoriasResultados(
                categorias = categorias,
                categoriaSeleccionada = categoriaSeleccionadaState,
                onCategoriaSeleccionada = { categoria ->
                    busquedaViewModel.seleccionarCategoria(categoria)
                }
            )

            ResultadosSeccion(
                establecimientos = establecimientos,
                categoria = categoriaSeleccionadaState ?: "todos los resultados",
                searchText = searchText,
                onEstablecimientoClick = { id ->
                    // Navegar a detalle del establecimiento
                    navController.navigate("${Pantalla.RUTA_NEGOCIODETALLE_APP}/$id")
                },
                onToggleFavorito = { id ->
                    // Manejar toggle de favorito
                    // TODO: Implementar lógica de favoritos
                }
            )
        }
    }
}

/**
 * Componente que muestra la lista de categorías para filtrar resultados.
 *
 * @param categorias Lista de categorías disponibles
 * @param categoriaSeleccionada Categoría actualmente seleccionada
 * @param onCategoriaSeleccionada Callback invocado cuando se selecciona una categoría
 * @param modifier Modificador de Composable para personalizar el layout
 */
@Composable
fun CategoriasResultados(
    categorias: List<Categoria>,
    categoriaSeleccionada: String?,
    onCategoriaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (categorias.isEmpty()) {
            Text(
                text = "No hay categorías disponibles",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            // Primera fila de categorías (4 elementos)
            val primeras4Categorias = categorias.take(4)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                primeras4Categorias.forEachIndexed { index, categoria ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(end = if (index < 3) 12.dp else 0.dp)
                    ) {
                        ItemCategoriaCirculoResultados(
                            categoria = categoria,
                            isSelected = categoria.nombre == categoriaSeleccionada,
                            onCategoriaClick = { onCategoriaSeleccionada(categoria.nombre) }
                        )
                        Text(
                            text = categoria.nombre,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (categoria.nombre == categoriaSeleccionada) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                            ),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(80.dp),
                            color = if (categoria.nombre == categoriaSeleccionada) Color(0xFF6200EE) else Color.Black
                        )
                    }
                }
            }

            // Segunda fila de categorías (3 elementos)
            val ultimas3Categorias = categorias.takeLast(3)
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
                        ItemCategoriaCirculoResultados(
                            categoria = categoria,
                            isSelected = categoria.nombre == categoriaSeleccionada,
                            onCategoriaClick = { onCategoriaSeleccionada(categoria.nombre) }
                        )
                        Text(
                            text = categoria.nombre,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (categoria.nombre == categoriaSeleccionada) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(80.dp),
                            color = if (categoria.nombre == categoriaSeleccionada) Color(0xFF6200EE) else Color.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Item individual de categoría en forma circular.
 *
 * @param categoria Datos de la categoría a mostrar
 * @param isSelected Indica si la categoría está seleccionada
 * @param onCategoriaClick Callback invocado cuando se hace clic en la categoría
 */
@Composable
fun ItemCategoriaCirculoResultados(
    categoria: Categoria,
    isSelected: Boolean,
    onCategoriaClick: () -> Unit
) {
    Card(
        onClick = onCategoriaClick,
        modifier = Modifier.size(70.dp),
        shape = RoundedCornerShape(100.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF6200EE) else Color.LightGray
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = categoria.iconoResId),
                contentDescription = categoria.nombre,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) Color.White else Color.Unspecified
            )
        }
    }
}

/**
 * Sección que muestra los resultados de establecimientos filtrados.
 *
 * @param establecimientos Lista de establecimientos a mostrar
 * @param categoria Categoría actual de filtrado
 * @param searchText Texto de búsqueda actual
 * @param onEstablecimientoClick Callback invocado cuando se hace clic en un establecimiento
 * @param onToggleFavorito Callback invocado cuando se togglea el estado de favorito
 * @param modifier Modificador de Composable para personalizar el layout
 */
@Composable
fun ResultadosSeccion(
    establecimientos: List<Establecimiento>,
    categoria: String,
    searchText: String,
    onEstablecimientoClick: (String) -> Unit,
    onToggleFavorito: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = if (searchText.isNotEmpty()) {
                "${establecimientos.size} resultados para \"$searchText\""
            } else {
                "${establecimientos.size} resultados en $categoria"
            },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (establecimientos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchText.isNotEmpty()) {
                        "No se encontraron resultados para \"$searchText\""
                    } else {
                        "No hay establecimientos en esta categoría"
                    },
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            establecimientos.forEach { establecimiento ->
                ItemEstablecimiento(
                    establecimiento = establecimiento,
                    onEstablecimientoClick = { onEstablecimientoClick(establecimiento.id) },
                    onToggleFavorito = { onToggleFavorito(establecimiento.id) },
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

/**
 * Item individual de establecimiento en la lista de resultados.
 *
 * @param establecimiento Datos del establecimiento a mostrar
 * @param onEstablecimientoClick Callback invocado cuando se hace clic en el establecimiento
 * @param onToggleFavorito Callback invocado cuando se togglea el estado de favorito
 * @param modifier Modificador de Composable para personalizar el layout
 */
@Composable
fun ItemEstablecimiento(
    establecimiento: Establecimiento,
    onEstablecimientoClick: () -> Unit,
    onToggleFavorito: () -> Unit,
    modifier: Modifier = Modifier
) {
    var esFavorito by remember { mutableStateOf(establecimiento.isFavorito) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEstablecimientoClick() }
    ) {
        AsyncImage(
            model = establecimiento.imagenUrl ?: "https://picsum.photos/200/300?random=${establecimiento.id}",
            contentDescription = "Imagen de ${establecimiento.nombre}",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = establecimiento.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${establecimiento.tiempo} – ${establecimiento.distancia} • ${establecimiento.rating} ⭐",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                )
            }

            IconButton(
                onClick = {
                    esFavorito = !esFavorito
                    onToggleFavorito()
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (esFavorito) Color.Red else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

/**
 * Previsualización de la pantalla de resultados.
 */
@Preview(showBackground = true)
@Composable
fun ResultadosPagePreview() {
    val navController = rememberNavController()
    ResultadosPage(navController)
}