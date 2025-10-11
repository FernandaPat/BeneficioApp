package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.R

@Composable
fun ResultadosPage(
    navController: NavController,
    categoriaSeleccionada: String = "Belleza",
    modifier: Modifier = Modifier
) {
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
            // Sección de Categorías
            CategoriasResultados(
                categoriaSeleccionada = categoriaSeleccionada,
                onCategoriaSeleccionada = { /* Manejar selección de categoría */ }
            )

            // Sección de Resultados (sin línea divisoria)
            ResultadosSeccion(
                categoria = categoriaSeleccionada,
                onEstablecimientoClick = { /* Navegar a detalle del establecimiento */ }
            )
        }
    }
}

@Composable
fun CategoriasResultados(
    categoriaSeleccionada: String,
    onCategoriaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Lista de categorías hardcodeada
    val categorias = listOf(
        "Belleza" to "💄",
        "Comida" to "🍕",
        "Educación" to "📚",
        "Entretenimiento" to "🎬",
        "Moda" to "👗",
        "Salud" to "🏥",
        "Servicios" to "🔧"
    )

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

        // Primera fila de categorías (4 elementos)
        val primeras4Categorias = categorias.take(4)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start,
        ) {
            primeras4Categorias.forEachIndexed { index, (nombre, icono) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(end = if (index < 3) 12.dp else 0.dp)
                ) {
                    ItemCategoriaCirculoResultados(
                        nombre = nombre,
                        icono = icono,
                        isSelected = nombre == categoriaSeleccionada,
                        onCategoriaClick = { onCategoriaSeleccionada(nombre) }
                    )
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (nombre == categoriaSeleccionada) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(80.dp),
                        color = if (nombre == categoriaSeleccionada) Color(0xFF6200EE) else Color.Black
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
            ultimas3Categorias.forEachIndexed { index, (nombre, icono) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(end = if (index < 2) 12.dp else 0.dp)
                ) {
                    ItemCategoriaCirculoResultados(
                        nombre = nombre,
                        icono = icono,
                        isSelected = nombre == categoriaSeleccionada,
                        onCategoriaClick = { onCategoriaSeleccionada(nombre) }
                    )
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (nombre == categoriaSeleccionada) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(80.dp),
                        color = if (nombre == categoriaSeleccionada) Color(0xFF6200EE) else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCategoriaCirculoResultados(
    nombre: String,
    icono: String,
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
            Text(
                text = icono,
                fontSize = 24.sp,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun ResultadosSeccion(
    categoria: String,
    onEstablecimientoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Datos hardcodeados para los establecimientos
    val establecimientos = listOf(
        EstablecimientoData("Establecimiento 1", "10 min", "1.6 km"),
        EstablecimientoData("Establecimiento 2", "10 min", "1.6 km"),
        EstablecimientoData("Establecimiento 3", "15 min", "2.1 km"),
        EstablecimientoData("Establecimiento 4", "8 min", "1.2 km"),
        EstablecimientoData("Establecimiento 5", "12 min", "1.8 km")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "${establecimientos.size} resultados para \"$categoria\"",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        establecimientos.forEach { establecimiento ->
            ItemEstablecimiento(
                establecimiento = establecimiento,
                onEstablecimientoClick = { onEstablecimientoClick(establecimiento.nombre) },
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
fun ItemEstablecimiento(
    establecimiento: EstablecimientoData,
    onEstablecimientoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var esFavorito by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEstablecimientoClick() }
    ) {
        // Placeholder para la imagen que se descargará de internet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Texto indicando que aquí irá la imagen
            Text(
                text = "Imagen de ${establecimiento.nombre}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.DarkGray,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        // Información del establecimiento con corazón a la derecha
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Información del establecimiento
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                    text = "${establecimiento.tiempo} – ${establecimiento.distancia}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                )
            }

            // Corazón de favoritos a la derecha
            IconButton(
                onClick = { esFavorito = !esFavorito },
                modifier = Modifier
                    .size(36.dp)
                    .padding(start = 8.dp)
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

// Data class para los establecimientos (sin imagen local)
data class EstablecimientoData(
    val nombre: String,
    val tiempo: String,
    val distancia: String
)

@Preview(showBackground = true)
@Composable
fun ResultadosPagePreview() {
    MaterialTheme {
        val navController = rememberNavController()
        ResultadosPage(navController)
    }
}