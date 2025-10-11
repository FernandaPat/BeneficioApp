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
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var descuento by rememberSaveable { mutableStateOf("") }
    var desde by rememberSaveable { mutableStateOf("") }
    var hasta by rememberSaveable { mutableStateOf("") }
    var expiraEn by rememberSaveable { mutableStateOf<Int?>(null) }

    val opciones = listOf("Belleza", "Comida", "Educación", "Salud", "Entretenimiento", "Moda", "Servicios")
    var categoria by rememberSaveable { mutableStateOf("") }


    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
        uri = result
    }

    Scaffold(
        topBar = { ArrowTopBar(navController, "Editar Promoción") },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(top = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (uri == null) {
                    IconButton(
                        onClick = { pickImage.launch("image/*") },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF7C3AED)
                        ),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Seleccionar imagen"
                        )
                    }
                } else {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Imagen de la promoción",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    // Botón para cambiar imagen
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
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Cambiar imagen",
                            tint = Color.White
                        )
                    }
                }
            }
            Etiqueta("Título", true)
            BeneficioOutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it.take(60) },
                placeholder = "Escribe aquí"
            )

            Etiqueta("Descripción", true)
            BeneficioOutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it.take(150) },
                placeholder = "Escribe aquí"
            )

            Etiqueta("Descuento", false)
            BeneficioOutlinedTextField(
                value = descuento,
                onValueChange = { descuento = it.take(20) },
                placeholder = "Ej. 10% o 2x1"
            )

            SeleccionarCategoria(
                categoria = categoria,
                onCategoriaChange = { categoria = it },
                categorias = opciones
            )
            RangoFechasPicker(
                desde = desde,
                hasta = hasta,
                onDesdeChange = { desde = it },
                onHastaChange = { fecha, dias ->
                    hasta = fecha
                    expiraEn = dias
                }
            )

            // Botón Guardar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BotonMorado(navController, "Editar", Pantalla.RUTA_INICIO_APP)
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
