package mx.mfpp.beneficioapp.view

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.viewmodel.EditarPromocionViewModel

/**
 * Pantalla para editar la información de una promoción existente.
 *
 * Permite al usuario modificar los datos básicos de la promoción:
 * - Imagen promocional
 * - Título
 * - Descripción
 * - Descuento
 * - Categoría
 * - Rango de fechas de validez
 *
 * Incluye la opción de reemplazar la imagen, y un botón final para guardar los cambios.
 *
 * @param navController Controlador de navegación de la aplicación.
 * @param modifier Modificador opcional para ajustar el diseño visual.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Editar_Promociones(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: EditarPromocionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scrollState = rememberScrollState()
    val promo = viewModel.promocion.value
    val uri = viewModel.nuevaImagenUri.value

    Scaffold(
        topBar = { ArrowTopBar(navController, "Editar Promoción") }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(top = 3.dp)
        ) {
            // 📸 Imagen
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                    viewModel.actualizarImagen(it)
                }

                if (uri == null && promo.imagenUrl == null) {
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
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Cambiar imagen", tint = Color.White)
                    }
                }
            }

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

            SeleccionarCategoria(
                categoria = promo.categoria,
                onCategoriaChange = viewModel::actualizarCategoria,
                categorias = listOf("Belleza", "Comida", "Educación", "Salud", "Entretenimiento", "Moda", "Servicios"),false
            )

            // 📅 Fechas (si lo usas)
            RangoFechasPicker(
                desde = "",
                hasta = "",
                onDesdeChange = {},
                onHastaChange = { _, dias -> viewModel.actualizarExpiraEn(dias) }
            )

            // 💾 Botón Guardar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BotonMorado(
                    texto = "Guardar cambios",
                    habilitado = true,
                    onClick = {
                        if (viewModel.guardarCambios()) {
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}

/**
 * Vista previa del diseño de la pantalla de edición de promociones.
 *
 * Se utiliza para inspeccionar la interfaz en el editor de Jetpack Compose
 * sin necesidad de ejecutar la aplicación completa.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Editar_PromocionesPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        Editar_Promociones(navController)
    }
}
