package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Data class para las categorías
data class Category(
    val name: String,
    val isSelected: Boolean = false
)

@Composable
fun CategoriesPage(navController: NavController) {
    // Lista de categorías con estado
    val categories = remember {
        mutableStateOf(
            listOf(
                Category("Belleza", true),
                Category("Comida", true),
                Category("Educación", true),
                Category("Salud", true),
                Category("Entretenimiento", true),
                Category("Moda", true),
                Category("Servicios", true)
            )
        )
    }

    // Estado para la búsqueda
    val searchText = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ArrowTopBar(navController, "Categorías")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
        ) {


            SearchBar(
                searchText = searchText.value,
                onSearchTextChanged = { searchText.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

        }
    }
}

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
            .background(Color(0xFFF5F5F5))
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
                            text = "Buscar categorías...",
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

@Composable
fun CategoryItem(
    category: Category,
    onCategoryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (category.isSelected) Color(0xFF7AF1A7) else Color.White
        ),
        onClick = onCategoryClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (category.isSelected) Color(0xFF008033) else Color.Black
            )

            if (category.isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = Color(0xFF008033),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CategoriesPreview() {
    val navController = rememberNavController()
    CategoriesPage(navController)
}