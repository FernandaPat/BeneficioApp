package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.model.Categoria
import mx.mfpp.beneficioapp.viewmodel.BusquedaViewModel
import mx.mfpp.beneficioapp.viewmodel.CategoriasViewModel

/**
 * Pantalla de exploración y búsqueda de establecimientos por categorías.
 *
 * Permite a los usuarios buscar establecimientos mediante texto o navegar
 * por categorías específicas para filtrar resultados.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas
 * @param categoriasViewModel ViewModel para categorías
 * @param busquedaViewModel ViewModel para búsqueda
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExplorarPage(
    navController: NavController,
    categoriasViewModel: CategoriasViewModel = viewModel(),
    busquedaViewModel: BusquedaViewModel = viewModel()
) {
    val categorias by categoriasViewModel.categorias.collectAsState()
    val categoriasLoading by categoriasViewModel.isLoading.collectAsState()
    val categoriasError by categoriasViewModel.error.collectAsState()

    val searchText by busquedaViewModel.textoBusqueda.collectAsState()

    // Estado local para el texto temporal de búsqueda
    var tempSearchText by remember { mutableStateOf("") }
    var shouldNavigateToSearch by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    // Cargar establecimientos al iniciar
    LaunchedEffect(Unit) {
        busquedaViewModel.cargarEstablecimientos(context)
        // Inicializar el texto temporal con el texto actual del ViewModel
        tempSearchText = searchText
    }

    // Navegar a resultados cuando se active la bandera
    LaunchedEffect(shouldNavigateToSearch) {
        if (shouldNavigateToSearch && tempSearchText.isNotEmpty()) {
            // Actualizar el ViewModel con el texto final
            busquedaViewModel.actualizarTextoBusqueda(tempSearchText)
            // Navegar a resultados
            navController.navigate("${Pantalla.RUTA_RESULTADOS_CON_TEXTO.replace("{query}", tempSearchText)}")
            shouldNavigateToSearch = false
            // Ocultar teclado
            keyboardController?.hide()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            SearchBar(
                searchText = tempSearchText,
                onSearchTextChanged = {
                    tempSearchText = it
                    // Solo actualizar el ViewModel localmente, sin navegar
                },
                onSearchExecute = {
                    if (tempSearchText.isNotEmpty()) {
                        shouldNavigateToSearch = true
                    }
                },
                focusRequester = focusRequester,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Text(
                text = "Categorías",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 16.dp)
            )

            when {
                categoriasLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cargando categorías...")
                    }
                }
                categoriasError != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error al cargar categorías",
                                color = Color.Red,
                                fontSize = 16.sp
                            )
                            Text(
                                text = categoriasError ?: "Error desconocido",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                categorias.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay categorías disponibles",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(categorias) { categoria ->
                            CategoryButton(
                                categoria = categoria,
                                onCategoryClicked = {
                                    println("🎯 ExplorarPage: Navegando a resultados con categoría: ${categoria.nombre}")
                                    busquedaViewModel.seleccionarCategoria(categoria.nombre)
                                    navController.navigate("${Pantalla.RUTA_RESULTADOS_CON_CATEGORIA.replace("{categoria}", categoria.nombre)}")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente que representa un botón de categoría en la lista de exploración.
 *
 * Muestra el nombre de la categoría con un icono y permite navegar a los resultados filtrados.
 *
 * @param categoria Datos de la categoría a mostrar
 * @param onCategoryClicked Callback invocado cuando se hace clic en la categoría
 */
@Composable
fun CategoryButton(
    categoria: Categoria,
    onCategoryClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCategoryClicked() }
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = categoria.iconoResId),
                contentDescription = "Categoría ${categoria.nombre}",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF9605F7)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = categoria.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Barra de búsqueda personalizada con campo de texto y icono de búsqueda.
 *
 * Permite a los usuarios ingresar texto para buscar establecimientos.
 *
 * @param searchText Texto actual de búsqueda
 * @param onSearchTextChanged Callback invocado cuando cambia el texto de búsqueda
 * @param onSearchExecute Callback invocado cuando se ejecuta la búsqueda (Enter o botón)
 * @param focusRequester Para manejar el foco del teclado
 * @param modifier Modificador de Composable para personalizar el layout
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchExecute: () -> Unit = {},
    focusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE0E4ED))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = searchText,
                onValueChange = onSearchTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchExecute()
                    }
                ),
                decorationBox = { innerTextField ->
                    if (searchText.isEmpty()) {
                        Text(
                            text = "Buscar establecimientos...",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

/**
 * Previsualización de la pantalla de exploración.
 */
@Preview(showBackground = true)
@Composable
fun CategoriesPreview() {
    val navController = rememberNavController()
    ExplorarPage(navController)
}