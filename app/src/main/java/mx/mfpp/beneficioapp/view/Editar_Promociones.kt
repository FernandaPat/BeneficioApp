package mx.mfpp.beneficioapp.view

import android.os.Build
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
fun Editar_Promociones(
    navController: NavController,
    idPromocion: Int,
    modifier: Modifier = Modifier,
    viewModel: EditarPromocionViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val promo = viewModel.promocion.value
    val uri = viewModel.nuevaImagenUri.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value

    // 🔹 Cargar los datos desde la API una sola vez
    LaunchedEffect(idPromocion) {
        viewModel.cargarPromocionPorId(idPromocion)
    }

    Scaffold(
        topBar = { ArrowTopBar(navController, "Editar Promoción") }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF9605F7))
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error, color = Color.Red)
                }
            }

            promo != null -> {
                Column(
                    modifier = modifier
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
                        val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                            viewModel.actualizarImagen(it)
                        }

                        if (uri == null && promo.imagenUrl.isNullOrEmpty()) {
                            IconButton(
                                onClick = { pickImage.launch("image/*") },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color(0xFF7C3AED)
                                ),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = "Seleccionar imagen")
                            }
                        } else {
                            AsyncImage(
                                model = uri ?: promo.imagenUrl,
                                contentDescription = "Imagen",
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
                                Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Cambiar imagen", tint = Color.White)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // 📝 Campos editables
                    Etiqueta("Título", false)
                    BeneficioOutlinedTextField(
                        value = promo.nombre,
                        onValueChange = viewModel::actualizarNombre,
                        placeholder = "Escribe aquí"
                    )

                    Etiqueta("Descripción", false)
                    BeneficioOutlinedTextField(
                        value = promo.descripcion ?: "",
                        onValueChange = viewModel::actualizarDescripcion,
                        placeholder = "Escribe aquí"
                    )

                    Etiqueta("Descuento", false)
                    BeneficioOutlinedTextField(
                        value = promo.descuento ?: "",
                        onValueChange = viewModel::actualizarDescuento,
                        placeholder = "Ej. 10% o 2x1"
                    )

                    // 💾 Botón Guardar
                    Spacer(Modifier.height(24.dp))
                    BotonMorado(
                        texto = "Guardar cambios",
                        habilitado = true,
                        onClick = {
                            scope.launch {
                                viewModel.guardarCambios()
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Editar_PromocionesPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        Editar_Promociones(navController = navController, idPromocion = 1)
    }
}
