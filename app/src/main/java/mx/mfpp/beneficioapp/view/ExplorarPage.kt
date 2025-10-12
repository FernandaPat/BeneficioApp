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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.model.Categoria
import mx.mfpp.beneficioapp.viewmodel.BeneficioJovenVM

/**
 * Pantalla de exploración y búsqueda de establecimientos por categorías.
 *
 * Permite a los usuarios buscar establecimientos mediante texto o navegar
 * por categorías específicas para filtrar resultados.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas
 * @param viewModel ViewModel que gestiona el estado y datos de búsqueda
 */
@Composable
fun ExplorarPage(
    navController: NavController,
    viewModel: BeneficioJovenVM = viewModel()
) {
    val categorias by viewModel.categorias.collectAsState()
    val searchText by viewModel.textoBusqueda.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            SearchBar(
                searchText = searchText,
                onSearchTextChanged = { viewModel.actualizarTextoBusqueda(it) },
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
                            viewModel.seleccionarCategoria(categoria.nombre)
                            navController.navigate("${Pantalla.RUTA_RESULTADOS_APP}/${categoria.nombre}")
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                painter = painterResource(id = R.drawable.circlestar),
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
 * @param modifier Modificador de Composable para personalizar el layout
 */
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    .background(Color.Transparent),
                singleLine = true,
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