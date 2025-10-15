package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaPage(navController: NavController, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val cdmx = LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cdmx, 13f)
    }

    var query by remember { mutableStateOf("") }

    val lugaresMock = listOf(
        Lugar("Café Roma", "10 min - 1.2 km", "4.6", LatLng(19.436, -99.14), "https://images.unsplash.com/photo-1555396273-367ea4eb4db5"),
        Lugar("Parque México", "5 min - 0.8 km", "4.8", LatLng(19.411, -99.17), "https://images.unsplash.com/photo-1505843513577-22bb7d21e455"),
        Lugar("Museo Soumaya", "18 min - 5.0 km", "4.7", LatLng(19.440, -99.202), "https://images.unsplash.com/photo-1601042879364-f36cde5f1c91"),
        Lugar("Restaurante El Cardenal", "12 min - 2.3 km", "4.9", LatLng(19.429, -99.135), "https://images.unsplash.com/photo-1552566626-52f8b828add9"),
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 230.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = Color.White,
        sheetDragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Gray.copy(alpha = 0.4f))
                    )
                }
            }
        },
        sheetContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(6.dp), //
                contentPadding = PaddingValues(bottom = 60.dp, top = 0.dp)
            ) {
                // Header SIN ESPACIO
                item {
                    Text(
                        text = "Cerca de ti",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 0.dp, bottom = 0.dp)
                    )
                }

                items(lugaresMock) { lugar ->
                    LugarCardModernCompact(
                        lugar,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {

                Marker(
                    state = MarkerState(position = cdmx),
                    title = "Tú estás aquí",
                    snippet = "Centro de CDMX",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )

                lugaresMock.forEach { lugar ->
                    Marker(
                        state = MarkerState(position = lugar.coordenadas),
                        title = lugar.nombre,
                        snippet = lugar.distancia,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                    )
                }
            }

            // --- Barra de búsqueda ---
            SearchBar2(
                searchText = query,
                onSearchTextChanged = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )
        }
    }
}

@Composable
fun LugarCardModernCompact(lugar: Lugar, modifier: Modifier = Modifier) {
    var esFavorito by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Navegar a detalle */ }
    ) {
        AsyncImage(
            model = lugar.imagen,
            contentDescription = lugar.nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp) // Aumentado para mejor visualización
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp), // MÍNIMO ABSOLUTO
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lugar.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(0.dp)) // SIN espacio
                Text(
                    text = "${lugar.distancia} • ${lugar.rating} ⭐",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            // Icono de corazón MÁS GRANDE
            IconButton(
                onClick = { esFavorito = !esFavorito },
                modifier = Modifier.size(32.dp) // AUMENTADO significativamente
            ) {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) Color.Red else Color.Gray,
                    modifier = Modifier.size(28.dp) // AUMENTADO significativamente
                )
            }
        }
    }
}

@Composable
fun SearchBar2(
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
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                modifier = Modifier.fillMaxWidth(),
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

@Composable
fun LugarCardModern(lugar: Lugar, modifier: Modifier = Modifier) {
    var esFavorito by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Navegar a detalle */ }
    ) {
        AsyncImage(
            model = lugar.imagen,
            contentDescription = lugar.nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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
                    text = lugar.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${lugar.distancia} • ${lugar.rating} ⭐",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = { esFavorito = !esFavorito },
                modifier = Modifier.size(32.dp) // AUMENTADO también en la versión original
            ) {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) Color.Red else Color.Gray,
                    modifier = Modifier.size(28.dp) // AUMENTADO también en la versión original
                )
            }
        }
    }
}

data class Lugar(
    val nombre: String,
    val distancia: String,
    val rating: String,
    val coordenadas: LatLng,
    val imagen: String
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MapaPreview() {
    val navController = rememberNavController()
    MapaPage(navController = navController)
}