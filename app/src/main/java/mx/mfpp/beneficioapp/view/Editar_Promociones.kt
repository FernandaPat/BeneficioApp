package mx.mfpp.beneficioapp.view

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.viewmodel.EditarPromocionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditarPromocion(
    navController: NavController,
    idPromocion: Int,
    viewModel: EditarPromocionViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados del ViewModel
    val nombre by viewModel.nombre.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val descuento by viewModel.descuento.collectAsState()
    val desde by viewModel.desde.collectAsState()
    val hasta by viewModel.hasta.collectAsState()
    val uri by viewModel.nuevaImagenUri.collectAsState()
    val imagenRemota by viewModel.imagenRemota.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 🔹 Cargar datos al abrir
    LaunchedEffect(idPromocion) {
        viewModel.cargarPromocionPorId(idPromocion)
    }

    // 🔹 Selector de imagen
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.actualizarImagen(it)
    }

    Scaffold(
        topBar = { ArrowTopBar(navController, "Editar Promoción") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 📸 Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                val imagenActual: Any? = uri ?: imagenRemota
                val tieneImagen = when (imagenActual) {
                    is Uri -> true
                    is String -> imagenActual.isNotEmpty()
                    else -> false
                }

                if (!tieneImagen) {
                    IconButton(
                        onClick = { pickImage.launch("image/*") },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF7C3AED)
                        ),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Seleccionar imagen")
                    }
                } else {
                    AsyncImage(
                        model = imagenActual,
                        contentDescription = "Imagen de la promoción",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    IconButton(
                        onClick = { pickImage.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Cambiar imagen", tint = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 🧾 Campos editables
            Etiqueta("Título", true)
            BeneficioOutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.actualizarNombre(it) },
                placeholder = "Escribe aquí"
            )

            Etiqueta("Descripción", true)
            BeneficioOutlinedTextField(
                value = descripcion,
                onValueChange = { viewModel.actualizarDescripcion(it) },
                placeholder = "Escribe aquí"
            )

            Etiqueta("Descuento", false)
            BeneficioOutlinedTextField(
                value = descuento,
                onValueChange = { viewModel.actualizarDescuento(it) },
                placeholder = "Ej. 10% o 2x1"
            )

            // 📅 Fechas
            RangoFechasPicker(
                desde = desde,
                hasta = hasta,
                onDesdeChange = { nuevaFechaDesde ->
                    viewModel.actualizarDesde(nuevaFechaDesde)
                    Log.d("FECHAS", "Desde seleccionada: $nuevaFechaDesde")
                },
                onHastaChange = { nuevaFechaHasta, _ ->
                    viewModel.actualizarHasta(nuevaFechaHasta)
                    Log.d("FECHAS", "Hasta seleccionada: $nuevaFechaHasta")
                }
            )

            Spacer(Modifier.height(40.dp))

            // 🟣 Botón centrado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                ButtonAction(
                    textoNormal = "Actualizar",
                    textoCargando = "Actualizando...",
                    isLoading = isLoading,
                    habilitado = true,
                    onClick = {
                        viewModel.actualizarPromocion(
                            context = context,
                            idPromocion = idPromocion,
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("✅ Promoción actualizada correctamente")
                                }
                                navController.navigate(Pantalla.RUTA_INICIO_NEGOCIO) {
                                    popUpTo(Pantalla.RUTA_EDITAR_PROMOCIONES) { inclusive = true }
                                }
                            },
                            onError = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        )
                    }
                )
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditarPromocionPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        EditarPromocion(navController = navController, idPromocion = 1)
    }
}
