package mx.mfpp.beneficioapp.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.model.Categoria
import mx.mfpp.beneficioapp.model.Establecimiento
import mx.mfpp.beneficioapp.model.FavoritoDetalle
import mx.mfpp.beneficioapp.model.PromocionJoven
import mx.mfpp.beneficioapp.model.ServicioRemotoFavoritos
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.utils.normalizarUrlImagen
import mx.mfpp.beneficioapp.viewmodel.BusquedaViewModel
import mx.mfpp.beneficioapp.viewmodel.CategoriasViewModel
import mx.mfpp.beneficioapp.viewmodel.PromocionJovenViewModel
import mx.mfpp.beneficioapp.viewmodel.VerDatosPersonalesViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
    promocionesViewModel: PromocionJovenViewModel = viewModel(),
    busquedaViewModel: BusquedaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val categorias by categoriasViewModel.categorias.collectAsState()
    val categoriasLoading by categoriasViewModel.isLoading.collectAsState()
    val categoriasError by categoriasViewModel.error.collectAsState()

    val nuevasPromociones by promocionesViewModel.nuevasPromociones.collectAsState()
    val promocionesExpiracion by promocionesViewModel.promocionesExpiracion.collectAsState()
    val todasPromociones by promocionesViewModel.todasPromociones.collectAsState()

    val promocionesLoading by promocionesViewModel.isLoading.collectAsState()
    val promocionesError by promocionesViewModel.error.collectAsState()

    val todosEstablecimientos by busquedaViewModel.establecimientos.collectAsState()
    val establecimientosLoading by busquedaViewModel.isLoading.collectAsState()
    val establecimientosError by busquedaViewModel.error.collectAsState()

    // CORREGIDO: Solo considerar loading de los ViewModels principales, no de favoritos
    val isLoading = categoriasLoading || promocionesLoading || establecimientosLoading
    val error = categoriasError ?: promocionesError ?: establecimientosError

    val sessionManager = remember { SessionManager(context) }
    val nombreJovenCompleto = sessionManager.getNombreJoven() ?: "Joven"
    val nombreJoven = nombreJovenCompleto.split(" ").firstOrNull() ?: "Joven"

    // Estado para los favoritos - CORREGIDO: No usar produceState aquí
    var listaFavoritos by remember { mutableStateOf<List<FavoritoDetalle>>(emptyList()) }
    var favoritosLoading by remember { mutableStateOf(false) }

    val fotoPerfil = sessionManager.getFotoPerfil()

    val vmDatosJoven: VerDatosPersonalesViewModel = viewModel()

    LaunchedEffect(Unit) {
        vmDatosJoven.cargarDatos(context)

        Log.d("INICIO_PAGE", "🧹 Limpiando filtros al entrar a InicioPage")
        busquedaViewModel.limpiarFiltrosCompletamente()
    }

    // Cargar favoritos cuando el usuario esté logueado
    LaunchedEffect(Unit) {
        val idUsuario = sessionManager.getJovenId()
        if (idUsuario != null && idUsuario != -1) {
            favoritosLoading = true
            try {
                val result = ServicioRemotoFavoritos.obtenerFavoritos(idUsuario)
                listaFavoritos = result.getOrElse { emptyList() }
                Log.d("INICIO_PAGE", "✅ Favoritos cargados: ${listaFavoritos.size}")
            } catch (e: Exception) {
                Log.e("INICIO_PAGE", "❌ Error cargando favoritos: ${e.message}")
            } finally {
                favoritosLoading = false
            }
        }
    }

    // Estado para controlar el refresh manual
    var isRefreshing by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var canTriggerRefresh by remember { mutableStateOf(true) }

    // Función para recargar todos los datos - CORREGIDA: Incluir favoritos
    fun recargarTodosLosDatos() {
        coroutineScope.launch {
            Log.d("INICIO_PAGE", "🔄 Recargando todos los datos...")
            busquedaViewModel.refrescarEstablecimientos(context)
            promocionesViewModel.refrescarPromociones()
            categoriasViewModel.refrescarCategorias()

            // Recargar favoritos también
            val idUsuario = sessionManager.getJovenId()
            if (idUsuario != null && idUsuario != -1) {
                try {
                    val result = ServicioRemotoFavoritos.obtenerFavoritos(idUsuario)
                    listaFavoritos = result.getOrElse { emptyList() }
                    Log.d("INICIO_PAGE", "✅ Favoritos recargados: ${listaFavoritos.size}")
                } catch (e: Exception) {
                    Log.e("INICIO_PAGE", "❌ Error recargando favoritos: ${e.message}")
                }
            }
        }
    }

    LaunchedEffect(scrollState.value) {
        val currentPosition = scrollState.value

        if (currentPosition == 0 && canTriggerRefresh && !isRefreshing && !isLoading) {
            isRefreshing = true
            canTriggerRefresh = false
            Log.d("INICIO_PAGE", "🔄 Iniciando refresh por scroll")

            try {
                recargarTodosLosDatos()
            } catch (e: Exception) {
                Log.e("INICIO_PAGE", "❌ Error durante refresh: ${e.message}")
            } finally {
                // Espera breve para mostrar el spinner y evitar flicker
                kotlinx.coroutines.delay(1200)
                isRefreshing = false
                canTriggerRefresh = true
                Log.d("INICIO_PAGE", "✅ Refresh completado correctamente")
            }
        }
    }

    // Recargar cuando la pantalla se enfoca por primera vez
    LaunchedEffect(Unit) {
        Log.d("INICIO_PAGE", "🔄 Cargando datos iniciales...")
        busquedaViewModel.cargarEstablecimientos(context)
        promocionesViewModel.refrescarPromociones()
        categoriasViewModel.refrescarCategorias()
    }

    // En tu InicioPage, después de cargar los favoritos, agrega:
    LaunchedEffect(listaFavoritos) {
        if (listaFavoritos.isNotEmpty()) {
            Log.d("FAVORITOS_ANALYSIS", "=== ANÁLISIS DE FAVORITOS ===")
            listaFavoritos.forEachIndexed { index, favorito ->
                Log.d("FAVORITOS_ANALYSIS",
                    "Favorito $index: ${favorito.nombre_establecimiento} | " +
                            "Foto: '${favorito.foto}' | " +
                            "Es null: ${favorito.foto == null} | " +
                            "Está vacío: ${favorito.foto?.isEmpty() ?: true}"
                )
            }
        }
    }
    Scaffold(
        topBar = { HomeTopBar(nombreJoven, fotoPerfil, navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Mostrar indicador de refresh manual SOLO cuando se activa por scroll
                if (isRefreshing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                strokeWidth = 3.dp
                            )
                            Text(
                                text = "Actualizando...",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                when {
                    isLoading && !isRefreshing -> {
                        Log.d("INICIO_PAGE", "⏳ Mostrando estado de carga")
                        EstadoCargando()
                    }
                    error != null -> {
                        Log.e("INICIO_PAGE", "❌ Mostrando error: $error")
                        EstadoError(
                            mensajeError = error,
                            onReintentar = {
                                recargarTodosLosDatos()
                            }
                        )
                    }
                    else -> {
                        Log.d("INICIO_PAGE", "✅ Mostrando datos - Categorías: ${categorias.size}, " +
                                "Nuevas Promos: ${nuevasPromociones.size}, " +
                                "Expiran: ${promocionesExpiracion.size}, " +
                                "Todas: ${todasPromociones.size}, " +
                                "Establecimientos: ${todosEstablecimientos.size}, " +
                                "Favoritos: ${listaFavoritos.size}")

                        Categorias(categorias = categorias, onCategoriaClick = { categoria ->
                            navController.navigate("${Pantalla.RUTA_RESULTADOS_APP}/${categoria.nombre}")
                        })

                        // Mostrar sección de favoritos solo si hay datos
                        if (listaFavoritos.isNotEmpty() || favoritosLoading) {
                            SeccionHorizontalFavoritos(
                                titulo = "Tus Favoritos",
                                favoritos = listaFavoritos,
                                isLoading = favoritosLoading,
                                onItemClick = { favorito ->
                                    navController.navigate("${Pantalla.RUTA_NEGOCIODETALLE_APP}/${favorito.id_establecimiento}")
                                }
                            )
                        }

                        SeccionHorizontal(
                            titulo = "Nuevas Promociones",
                            items = nuevasPromociones,
                            promocionesViewModel = promocionesViewModel,
                            onItemClick = { promocion ->
                                // 🔹 CAMBIO: Pasar el ID de la promoción
                                navController.navigate("qrPromocion/${promocion.id}")
                            }
                        )

                        SeccionHorizontal(
                            titulo = "Expiran pronto",
                            items = promocionesExpiracion,
                            promocionesViewModel = promocionesViewModel,
                            onItemClick = { promocion ->
                                // 🔹 CAMBIO: Pasar el ID de la promoción
                                navController.navigate("qrPromocion/${promocion.id}")
                            }
                        )

                        SeccionHorizontal(
                            titulo = "Todas las promociones",
                            items = todasPromociones,
                            promocionesViewModel = promocionesViewModel,
                            onItemClick = { promocion ->
                                // 🔹 CAMBIO: Pasar el ID de la promoción
                                navController.navigate("qrPromocion/${promocion.id}")
                            }
                        )

                        SeccionHorizontalEstablecimientos(
                            titulo = "Todos los establecimientos",
                            establecimientos = todosEstablecimientos,
                            onItemClick = { establecimiento ->
                                navController.navigate("${Pantalla.RUTA_NEGOCIODETALLE_APP}/${establecimiento.id_establecimiento}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionHorizontalEstablecimientos(
    titulo: String,
    establecimientos: List<Establecimiento>,
    onItemClick: (Establecimiento) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("UI_DEBUG", "🏪 Renderizando sección: $titulo con ${establecimientos.size} establecimientos")

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

        if (establecimientos.isEmpty()) {
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
                items(establecimientos) { establecimiento ->
                    CardEstablecimientoHorizontal(
                        establecimiento = establecimiento,
                        onItemClick = { onItemClick(establecimiento) }
                    )
                }
            }
        }
    }
}

// NUEVO COMPONENTE: Card para establecimientos (similar al de promociones)
@Composable
fun CardEstablecimientoHorizontal(
    establecimiento: Establecimiento,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier.width(176.dp)
    ) {
        Card(
            onClick = onItemClick,
            modifier = Modifier
                .size(width = 176.dp, height = 100.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Imagen del establecimiento
                AsyncImage(
                    model = establecimiento.imagen ?: "https://picsum.photos/200/150?random=${establecimiento.id_establecimiento}",
                    contentDescription = "Imagen de ${establecimiento.nombre}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Información del establecimiento
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            // Nombre del establecimiento
            Text(
                text = establecimiento.nombre,
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )

            // Categoría y ubicación
            Text(
                text = "${establecimiento.nombre_categoria} • ${establecimiento.colonia}",
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 0.dp),
                lineHeight = 12.sp
            )
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
    items: List<PromocionJoven>,
    promocionesViewModel: PromocionJovenViewModel,
    onItemClick: (PromocionJoven) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("UI_DEBUG", "🔄 Renderizando sección: $titulo con ${items.size} items")

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
                    CardItemHorizontal(
                        promocion = item,
                        promocionesViewModel = promocionesViewModel,
                        esNuevaSeccion = titulo == "Nuevas Promociones", // Pasar si es nueva sección
                        onItemClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun CardItemHorizontal(
    promocion: PromocionJoven,
    promocionesViewModel: PromocionJovenViewModel,
    esNuevaSeccion: Boolean = false,
    onItemClick: () -> Unit
) {

    // Texto y color según la sección
    val (textoEstado, colorFondo, colorTexto) = if (esNuevaSeccion) {
        val diasDesdeCreacion = promocionesViewModel.diasDesdeCreacion(promocion.fecha_creacion)
        val textoCreacion = when {
            diasDesdeCreacion < 0 -> "Fecha inválida"
            diasDesdeCreacion == 0L -> "hoy"
            diasDesdeCreacion == 1L -> "ayer"
            diasDesdeCreacion <= 7L -> "hace $diasDesdeCreacion días"
            else -> "hace $diasDesdeCreacion días"
        }
        Triple(
            textoCreacion,
            Color(0xFF7AF1A7), // Verde claro para "agregada"
            Color(0xFF008033)  // Verde oscuro
        )
    } else {
        val textoExpiracion = promocionesViewModel.formatearTextoExpiracion(promocion.fecha_expiracion)
        val (fondo, texto) = when {
            textoExpiracion.contains("hoy") -> Color(0xFFFFA500) to Color(0xFF8B4513) // Naranja
            textoExpiracion.contains("1 día") -> Color(0xFFFFA500) to Color(0xFF8B4513) // Naranja para "vence en 1 día"
            textoExpiracion.contains("días") -> Color(0xFFFFA500) to Color(0xFF8B4513) // Naranja
            textoExpiracion.contains("Expirada") -> Color(0xFFBDBDBD) to Color(0xFF616161) // Gris
            else -> Color(0xFF7AF1A7) to Color(0xFF008033) // Verde para válida
        }
        Triple(textoExpiracion, fondo, texto)
    }

    Column(
        modifier = Modifier.width(176.dp)
    ) {
        Card(
            onClick = onItemClick,
            modifier = Modifier
                .size(width = 176.dp, height = 100.dp), // Rectangular
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Imagen rectangular
                AsyncImage(
                    model = promocion.foto ?: "https://picsum.photos/200/150?random=${promocion.id}",
                    contentDescription = "Imagen de ${promocion.titulo}", // CAMBIO: titulo_promocion -> titulo
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Badge de estado con offset forzado
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .width(if (textoEstado == "hoy" || textoEstado == "ayer") 50.dp else 80.dp)
                        .height(18.dp) // Mismo alto para todos
                        .background(
                            color = colorFondo,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Text(
                        text = textoEstado.uppercase(),
                        fontSize = 8.sp,
                        color = colorTexto,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                            .offset(y = (-1).dp) // Offset forzado para centrado vertical
                    )
                }
            }
        }

        // Información muy compacta
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp) // Mínimo espaciado
        ) {

            Text(
                text = promocion.titulo,
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )

            // Título de la promoción - CAMBIO: titulo_promocion -> titulo
            Text(
                text = promocion.nombre_establecimiento, // CAMBIO AQUÍ
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 0.dp), // Sin espaciado
                lineHeight = 12.sp
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
fun Categorias(
    categorias: List<Categoria>,
    onCategoriaClick: (Categoria) -> Unit
) {
    Log.d("UI_DEBUG", "📊 Renderizando categorías: ${categorias.size} categorías")

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
            val primeras4 = categorias.take(4)
            val ultimas3 = categorias.takeLast(3)

            Row(modifier = Modifier.fillMaxWidth()) {
                primeras4.forEachIndexed { index, categoria ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = if (index < 3) 12.dp else 0.dp)
                    ) {
                        ItemCategoriaCirculo(categoria = categoria, onClick = {
                            onCategoriaClick(categoria)
                        })
                        Text(
                            text = categoria.nombre,
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .width(80.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                ultimas3.forEachIndexed { index, categoria ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = if (index < 2) 12.dp else 0.dp)
                    ) {
                        ItemCategoriaCirculo(categoria = categoria, onClick = {
                            onCategoriaClick(categoria)
                        })
                        Text(
                            text = categoria.nombre,
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .width(80.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
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
fun ItemCategoriaCirculo(categoria: Categoria, onClick: () -> Unit) {
    Card(
        onClick = onClick,
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
fun HomeTopBar(
    nombreJoven: String,
    fotoPerfil: String?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
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
                    if (!fotoPerfil.isNullOrBlank()) {
                        AsyncImage(
                            model = fotoPerfil.replace(" ", "%20"),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
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
                    text = "Hola, $nombreJoven",
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


@Composable
fun SeccionHorizontalFavoritos(
    titulo: String,
    favoritos: List<FavoritoDetalle>,
    isLoading: Boolean = false,
    onItemClick: (FavoritoDetalle) -> Unit,
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

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    strokeWidth = 3.dp
                )
            }
        } else if (favoritos.isEmpty()) {
            Text(
                text = "No tienes favoritos aún",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favoritos) { favorito ->
                    CardFavoritoHorizontal(favorito, onItemClick)
                }
            }
        }
    }
}

@Composable
fun CardFavoritoHorizontal(
    favorito: FavoritoDetalle,
    onItemClick: (FavoritoDetalle) -> Unit
) {
    Column(
        modifier = Modifier.width(176.dp)
    ) {
        Card(
            onClick = { onItemClick(favorito) },
            modifier = Modifier
                .size(width = 176.dp, height = 100.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // VERSIÓN SIMPLIFICADA - misma lógica que establecimientos

                val imagenUrl = normalizarUrlImagen(favorito.foto, favorito.id_establecimiento)

                Log.d("FAVORITO_IMAGE", "URL final: $imagenUrl")

                AsyncImage(
                    model = imagenUrl,
                    contentDescription = "Imagen de ${favorito.nombre_establecimiento}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Información del establecimiento
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text(
                text = favorito.nombre_establecimiento,
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )

            Text(
                text = "${favorito.nombre_categoria ?: "Sin categoría"} • ${favorito.colonia ?: ""}",
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 0.dp),
                lineHeight = 12.sp
            )
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
        //InicioPage(navController)
    }
}