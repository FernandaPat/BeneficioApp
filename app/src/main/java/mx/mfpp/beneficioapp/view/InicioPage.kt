package mx.mfpp.beneficioapp.view

import mx.mfpp.beneficioapp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Data classes (pasar al Model después)
data class Categoria(
    val nombre: String,
    val icono: Int? = null
)

data class Promocion(
    val nombre: String,
    val imagenUrl: String? = null,
    val descuento: String? = null
)

// Listas de datos (pasar al ViewModel después)
val categorias = listOf(
    Categoria("Belleza"),
    Categoria("Comida"),
    Categoria("Educación"),
    Categoria("Moda"),
    Categoria("Servicios"),
    Categoria("Salud"),
    Categoria("Ocio")
)

val favoritos = listOf(
    Promocion("Kinezis"),
    Promocion("Six Flags"),
    Promocion("Pinche elote"),
    Promocion("H&M")
)

val nuevasPromociones = listOf(
    Promocion("Burger King"),
    Promocion("Cinemex"),
    Promocion("Porrúa"),
    Promocion("Museo Soumaya")
)

val promocionesExpiracion = listOf(
    Promocion("Suburbia"),
    Promocion("Oxxo"),
    Promocion("3B")
)

val cercaDeTi = listOf(
    Promocion("Chilaquiles TEC"),
    Promocion("Helados Froddy"),
    Promocion("Carls Jr")
)

@Composable
fun InicioPage(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { HomeTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.
                background(Color(0xFF230448))
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Categorias()
            SeccionHorizontal(
                titulo = "Tus Favoritos",
                items = favoritos
            )
            SeccionHorizontal(
                titulo = "Nuevas Promociones",
                items = nuevasPromociones
            )
            SeccionHorizontal(
                titulo = "Expiran pronto",
                items = promocionesExpiracion
            )
            SeccionHorizontal(
                titulo = "Cerca de ti",
                items = cercaDeTi
            )
        }
    }
}

@Composable
fun SeccionHorizontal(
    titulo: String,
    items: List<Promocion>,
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
                fontSize = 24.sp,
                color = Color.White
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                CardItemHorizontal(promocion = item)
            }
        }
    }
}

@Composable
fun CardItemHorizontal(promocion: Promocion){
    Card(
        onClick = { /* Acción al hacer clic */ },
        modifier = Modifier
            .size(width = 176.dp, height = 108.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF170033))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = promocion.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                promocion.descuento?.let { descuento ->
                    Text(
                        text = descuento,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun Categorias() {
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
                color = Color.White
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val primeras4Categorias = categorias.take(4)
        val ultimas3Categorias = categorias.takeLast(3)

        // Primera fila con 4 categorías
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
                        color = Color.White
                    )
                }
            }
        }

        // Segunda fila con 3 categorías
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
                        color = Color.White
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
        modifier = Modifier
            .size(70.dp),
        shape = RoundedCornerShape(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF170033))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Icono de las categorías (agregar después)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavController,modifier: Modifier = Modifier) {
    val barHeight = 120.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .background(Color(0xF230448))
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(93.dp)
                        .padding(end = 12.dp),
                    tint = Color.White
                )
                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Color.White

                    )
                )
            }

            IconButton(
                onClick = {
                    navController.navigate(Pantalla.RUTA_NOTIFICACIONES_APP)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bell),
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePage() {
    val navController = rememberNavController()
    InicioPage(navController)
}
