import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.math.*
import mx.mfpp.beneficioapp.model.Establecimiento
import mx.mfpp.beneficioapp.viewmodel.MapaViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Close
import mx.mfpp.beneficioapp.view.Pantalla
/**
 * Pantalla de mapa que muestra establecimientos y la ubicación actual del usuario.
 *
 * Funcionalidades principales:
 * - Solicita y gestiona permisos de ubicación.
 * - Obtiene la ubicación actual del usuario usando FusedLocationProviderClient.
 * - Muestra la ubicación de los establecimientos en Google Maps con marcadores.
 * - Permite filtrar para mostrar un solo negocio si se proporcionan latitud y longitud.
 * - Muestra un BottomSheet con lista de establecimientos, incluyendo distancia desde el usuario.
 * - Maneja estados de carga, errores y recarga de datos.
 *
 * @param navController Controlador de navegación para mover a otras pantallas.
 * @param lat Latitud opcional para centrar el mapa en un negocio específico.
 * @param lng Longitud opcional para centrar el mapa en un negocio específico.
 * @param modifier Modificador opcional de Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaPage(
    navController: NavController,
    lat: Double? = null,
    lng: Double? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: MapaViewModel = viewModel()

    // ---------------------- Observables ----------------------
    val establecimientos by viewModel.establecimientosFiltrados.collectAsState()
    val establecimientosOrdenados by viewModel.establecimientosOrdenados.collectAsState()
    val ubicacionActual by viewModel.ubicacionActual.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var hasLocationPermission by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L).build()
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val loc = locationResult.lastLocation
                loc?.let {
                    val nuevaUbicacion = LatLng(it.latitude, it.longitude)
                    currentLocation = nuevaUbicacion
                    viewModel.actualizarUbicacionActual(nuevaUbicacion)
                }
            }
        }
    }

    val mostrarSoloUnNegocio = lat != null && lng != null

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val cameraPositionState = rememberCameraPositionState()

    // ---------------------- Cámara ----------------------
    LaunchedEffect(lat, lng) {
        if (lat != null && lng != null) {
            val posicionNegocio = LatLng(lat, lng)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(posicionNegocio, 17f),
                durationMs = 1000
            )
        }
    }

    LaunchedEffect(currentLocation) {
        if (currentLocation != null && !mostrarSoloUnNegocio) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f),
                durationMs = 1000
            )
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    loc?.let {
                        val ubicacion = LatLng(it.latitude, it.longitude)
                        currentLocation = ubicacion
                        viewModel.actualizarUbicacionActual(ubicacion)
                    }
                }
            } else {
                Toast.makeText(context, "Es necesario otorgar permiso de ubicación", Toast.LENGTH_SHORT).show()
            }
        }

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
                null
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

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // ---------------------- Sheet ----------------------
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 280.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = Color.White,
        sheetDragHandle = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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
            val listaParaSheet = if (mostrarSoloUnNegocio) {
                establecimientosOrdenados.filter { est ->
                    est.latitud != null && est.longitud != null &&
                            approxEqual(est.latitud!!, lat!!) &&
                            approxEqual(est.longitud!!, lng!!)
                }
            } else {
                establecimientosOrdenados
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {
                item {
                    Text(
                        text = when {
                            mostrarSoloUnNegocio -> "Ubicación del negocio"
                            ubicacionActual != null -> "Establecimientos cercanos"
                            else -> "Todos los establecimientos"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    )
                }

                // ✅ CORREGIDO: Mejor manejo de estados de carga
                when {
                    isLoading -> {
                        // Muestra indicador de carga
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = Color(0xFF6200EE),
                                        strokeWidth = 3.dp
                                    )
                                    Text(
                                        text = "Cargando establecimientos...",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                    listaParaSheet.isEmpty() -> {
                        // Solo muestra "no disponibles" cuando NO está cargando y la lista está vacía
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay establecimientos disponibles",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    else -> {
                        // Muestra la lista cuando hay datos
                        items(listaParaSheet, key = { it.id_establecimiento }) { est ->
                            EstablecimientoCard(
                                establecimiento = est,
                                ubicacionActual = ubicacionActual,
                                onItemClick = {
                                    navController.navigate("${Pantalla.RUTA_NEGOCIODETALLE_APP}/${est.id_establecimiento}")
                                },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // ---------------------- Google Map ----------------------
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission)
            ) {
                if (mostrarSoloUnNegocio) {
                    Marker(
                        state = MarkerState(LatLng(lat!!, lng!!)),
                        title = "Ubicación del negocio",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                } else {
                    // ✅ MEJORADO: Solo renderizar marcadores cuando no está cargando
                    if (!isLoading) {
                        establecimientosOrdenados.forEach { est ->
                            est.latitud?.let { la ->
                                est.longitud?.let { lo ->
                                    Marker(
                                        state = MarkerState(LatLng(la, lo)),
                                        title = est.nombre,
                                        snippet = "${est.nombre_categoria} • ${est.colonia}",
                                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                                    )
                                }
                            }
                        }
                    }
                }

                currentLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Mi ubicación actual",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }
            }

            // ✅ AGREGADO: Indicador de carga sobre el mapa
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFF6200EE),
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "Buscando establecimientos cercanos...",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.background(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(8.dp)
                            ).padding(12.dp)
                        )
                    }
                }
            }

            // ✅ AGREGADO: Botón de recarga para casos de error o timeout
            if (!isLoading && error == null && establecimientosOrdenados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { viewModel.refrescarEstablecimientos() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reintentar carga")
                    }
                }
            }

            // ✅ AGREGADO: Mostrar error específico en el mapa
            error?.let { errorMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFE6E6)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearError() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar error",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
/**
 * Composable que representa una tarjeta de establecimiento en la lista del BottomSheet.
 *
 * Muestra:
 * - Imagen del establecimiento.
 * - Nombre, categoría, colonia y distancia desde la ubicación actual.
 * - Clickable para navegar al detalle del establecimiento.
 *
 * @param establecimiento Objeto que contiene los datos del establecimiento.
 * @param ubicacionActual Ubicación actual del usuario para calcular la distancia.
 * @param onItemClick Callback que se ejecuta al presionar la tarjeta.
 * @param modifier Modificador opcional de Compose.
 */
@Composable
fun EstablecimientoCard(
    establecimiento: Establecimiento,
    ubicacionActual: LatLng?,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val distanciaTexto = remember(establecimiento, ubicacionActual) {
        if (ubicacionActual != null && establecimiento.latitud != null && establecimiento.longitud != null) {
            val distancia = calcularDistancia(
                ubicacionActual,
                LatLng(establecimiento.latitud!!, establecimiento.longitud!!)
            )
            "• ${formatearDistancia(distancia)}"
        } else ""
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
    ) {
        AsyncImage(
            model = establecimiento.imagen ?: "https://picsum.photos/200/150?random=${establecimiento.id_establecimiento}",
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
        }
    }
}
/**
 * Calcula la distancia en metros entre dos coordenadas LatLng usando la fórmula de Haversine.
 *
 * @param p1 Primera coordenada.
 * @param p2 Segunda coordenada.
 * @return Distancia en metros.
 */
private fun calcularDistancia(p1: LatLng, p2: LatLng): Double {
    val R = 6371000.0
    val lat1 = Math.toRadians(p1.latitude)
    val lon1 = Math.toRadians(p1.longitude)
    val lat2 = Math.toRadians(p2.latitude)
    val lon2 = Math.toRadians(p2.longitude)
    val dLat = lat2 - lat1
    val dLon = lon2 - lon1
    val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}
/**
 * Formatea una distancia en metros a una cadena legible.
 *
 * - Si es menor a 1000 m, se muestra en metros.
 * - Si es mayor o igual a 1000 m, se muestra en kilómetros con un decimal.
 *
 * @param metros Distancia en metros.
 * @return Distancia formateada como texto.
 */
private fun formatearDistancia(metros: Double): String =
    if (metros < 1000) "${metros.toInt()} m" else "${(metros / 1000).format(1)} km"

private fun Double.format(digits: Int) = "%.${digits}f".format(this)
/**
 * Aproxima la igualdad de dos valores Double dentro de un margen de error.
 *
 * @param a Primer valor.
 * @param b Segundo valor.
 * @param eps Margen de tolerancia.
 * @return true si los valores son aproximadamente iguales, false en caso contrario.
 */
private fun approxEqual(a: Double, b: Double, eps: Double = 0.00001): Boolean =
    kotlin.math.abs(a - b) <= eps