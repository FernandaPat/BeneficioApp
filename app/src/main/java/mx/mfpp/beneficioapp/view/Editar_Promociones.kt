package mx.mfpp.beneficioapp.view

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.viewmodel.EditarPromocionViewModel
import java.net.URI

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Editar_Promociones(
    navController: NavController,
    idPromocion: Int,
    viewModel: EditarPromocionViewModel = viewModel()
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 📸 Selector de imagen
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.uri.value = URI(it.toString())
            viewModel.imagenUrl.value = it.toString()
        }
    }

    // ⚡ Cargar datos al iniciar
    LaunchedEffect(idPromocion) {
        println("🟣 Navegando a editarPromocion/$idPromocion")
        viewModel.cargarPromocion(idPromocion) {}
    }

    Scaffold(
        topBar = { ArrowTopBar(navController, "Editar Promoción") },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scroll)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // 📸 IMAGEN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.imagenUrl.collectAsState().value.isEmpty()) {
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
                        model = viewModel.imagenUrl.collectAsState().value,
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
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Cambiar imagen",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🧾 CAMPOS DE TEXTO
            Etiqueta("Título", true)
            BeneficioOutlinedTextField(
                value = viewModel.nombre.collectAsState().value,
                onValueChange = { viewModel.nombre.value = it },
                placeholder = "Título de la promoción"
            )

            Etiqueta("Descripción", true)
            BeneficioOutlinedTextField(
                value = viewModel.descripcion.collectAsState().value,
                onValueChange = { viewModel.descripcion.value = it },
                placeholder = "Descripción breve"
            )

            Etiqueta("Descuento", false)
            BeneficioOutlinedTextField(
                value = viewModel.descuento.collectAsState().value,
                onValueChange = { viewModel.descuento.value = it },
                placeholder = "Ej. 20% o 2x1"
            )

            // 📅 FECHAS
            Etiqueta("Periodo de validez", true)
            RangoFechasPicker(
                desde = viewModel.desde.collectAsState().value,
                hasta = viewModel.hasta.collectAsState().value,
                onDesdeChange = { viewModel.desde.value = it },
                onHastaChange = { fecha, _ -> viewModel.hasta.value = fecha }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 💾 BOTÓN GUARDAR
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonAction(
                    textoNormal = "Guardar Cambios",
                    textoCargando = "Actualizando...",
                    isLoading = viewModel.isLoading.collectAsState().value,
                    habilitado = true,
                    onClick = {
                        viewModel.actualizarPromocion(
                            idPromocion = idPromocion,
                            context = context,
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("✅ Promoción actualizada con éxito")
                                }
                                navController.popBackStack()
                            },
                            onError = { msg ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
