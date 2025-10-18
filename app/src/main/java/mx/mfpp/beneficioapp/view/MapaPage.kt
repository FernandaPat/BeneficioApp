// mx.mfpp.beneficioapp.view.MapaPage
package mx.mfpp.beneficioapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.math.*
import mx.mfpp.beneficioapp.model.Establecimiento
import mx.mfpp.beneficioapp.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaPage(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: MapaViewModel = viewModel()

    val establecimientos by viewModel.establecimientosFiltrados.collectAsState()
    val establecimientosConCoordenadas by remember {
        derivedStateOf { viewModel.establecimientosConCoordenadas }
    }
    val establecimientosOrdenados by remember {
        derivedStateOf { viewModel.establecimientosOrdenadosPorDistancia }
    }
    val ubicacionActual by viewModel.ubicacionActual.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // --- Permiso y ubicación ---
    var hasLocationPermission by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // --- Configurar request y callback ---
    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val loc = locationResult.lastLocation
                if (loc != null) {
                    val nuevaUbicacion = LatLng(loc.latitude, loc.longitude)
                    currentLocation = nuevaUbicacion
                    // Actualizar la ubicación en el ViewModel para calcular distancias
                    viewModel.actualizarUbicacionActual(nuevaUbicacion)
                }
            }
        }
    }

    // --- Estado del mapa y hoja ---
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val cameraPositionState = rememberCameraPositionState()

    // --- Búsqueda ---
    var query by remember { mutableStateOf("") }

    // --- Permiso launcher ---
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    loc?.let {
                        val ubicacion = LatLng(it.latitude, it.longitude)
                        currentLocation = ubicacion
                        viewModel.actualizarUbicacionActual(ubicacion)
                    }
                }
            } else {
                Toast.makeText(context, "Es necesario que se otorgue permiso para la ubicación", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    // --- Verificar permiso al iniciar ---
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasLocationPermission = true
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    val ubicacion = LatLng(it.latitude, it.longitude)
                    currentLocation = ubicacion
                    viewModel.actualizarUbicacionActual(ubicacion)
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Cuando se obtiene ubicación, centramos el mapa automáticamente
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
            viewModel.actualizarUbicacionActual(it)
        }
    }

    // Filtrar establecimientos cuando cambia la query
    LaunchedEffect(query) {
        viewModel.filtrarEstablecimientos(query)
    }

    // Manejar errores
    LaunchedEffect(error) {
        error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 230.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = Color.White,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
        },
        sheetContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {
                item {
                    Text(
                        text = if (ubicacionActual != null) "Establecimientos cercanos" else "Todos los establecimientos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (establecimientosOrdenados.isEmpty()) {
                    item {
                        Text(
                            text = "No hay establecimientos disponibles",
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                } else {
                    items(items = establecimientosOrdenados, key = { it.id_establecimiento }) { establecimiento ->
                        EstablecimientoCard(
                            establecimiento = establecimiento,
                            ubicacionActual = ubicacionActual,
                            onItemClick = {
                                navController.navigate("${Pantalla.RUTA_NEGOCIODETALLE_APP}/${establecimiento.id_establecimiento}")
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission)
            ) {
                // Marcador de ubicación actual
                currentLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = "Mi ubicación actual",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                    )
                }

                // Marcadores de establecimientos con coordenadas
                establecimientosConCoordenadas.forEach { establecimiento ->
                    establecimiento.latitud?.let { lat ->
                        establecimiento.longitud?.let { lng ->
                            val coordenadas = LatLng(lat, lng)
                            Marker(
                                state = MarkerState(position = coordenadas),
                                title = establecimiento.nombre,
                                snippet = "${establecimiento.nombre_categoria} • ${establecimiento.colonia}",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                            )
                        }
                    }
                }
            }

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
fun EstablecimientoCard(
    establecimiento: Establecimiento,
    ubicacionActual: LatLng?,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var esFavorito by remember { mutableStateOf(false) }

    // Calcular distancia si tenemos ubicación actual
    val distanciaTexto = remember(establecimiento, ubicacionActual) {
        if (ubicacionActual != null && establecimiento.latitud != null && establecimiento.longitud != null) {
            val distancia = calcularDistancia(
                ubicacionActual,
                LatLng(establecimiento.latitud!!, establecimiento.longitud!!)
            )
            "• ${formatearDistancia(distancia)}"
        } else {
            ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
    ) {
        AsyncImage(
            model = establecimiento.foto ?: "https://picsum.photos/200/150?random=${establecimiento.id_establecimiento}",
            contentDescription = establecimiento.nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = establecimiento.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${establecimiento.nombre_categoria} • ${establecimiento.colonia} $distanciaTexto",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
            IconButton(
                onClick = { esFavorito = !esFavorito },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) Color.Red else Color.Gray,
                    modifier = Modifier.size(28.dp)
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

// Funciones auxiliares para calcular distancia
private fun calcularDistancia(punto1: LatLng, punto2: LatLng): Double {
    val radioTierra = 6371000.0 // Radio de la Tierra en metros

    val lat1 = Math.toRadians(punto1.latitude)
    val lon1 = Math.toRadians(punto1.longitude)
    val lat2 = Math.toRadians(punto2.latitude)
    val lon2 = Math.toRadians(punto2.longitude)

    val dLat = lat2 - lat1
    val dLon = lon2 - lon1

    val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return radioTierra * c
}

private fun formatearDistancia(metros: Double): String {
    return when {
        metros < 1000 -> "${metros.toInt()} m"
        else -> "${(metros / 1000).format(1)} km"
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)