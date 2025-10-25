package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.viewmodel.PromocionesViewModel
/**
 * Pantalla que muestra la lista de promociones de un negocio.
 *
 * Funcionalidades principales:
 * - Carga las promociones desde la ViewModel asociada al negocio actual.
 * - Permite agregar una nueva promoción mediante un FloatingActionButton.
 * - Cada item de promoción permite editar o eliminar la promoción.
 * - Muestra un Snackbar con el mensaje de operación completada.
 * - Maneja el diálogo de confirmación antes de eliminar una promoción.
 *
 * @param navController Controlador de navegación para moverse entre pantallas.
 * @param viewModel ViewModel asociado a las promociones.
 * @param modifier Modificador opcional para personalizar la apariencia del layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Promociones(
    navController: NavController,
    viewModel: PromocionesViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val promociones by viewModel.promociones.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDeleteId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isDeleting by viewModel.isDeleting.collectAsState()

    LaunchedEffect(Unit) {
        val idNegocio = sessionManager.getNegocioId() ?: 0
        if (idNegocio != 0) {
            viewModel.cargarPromociones(idNegocio)
        }
    }


    Scaffold(
        topBar = { ArrowTopBar(navController, "Promociones") },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Pantalla.RUTA_AGREGAR_PROMOCIONES) },
                containerColor = Color(0xFF9605F7),
                contentColor = Color.White,
                shape = RoundedCornerShape(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar promoción")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            itemsIndexed(promociones, key = { _, p -> p.id }) { index, promo ->
                PromoListItem(
                    promo = promo,
                    onEdit = {
                        println("🟣 Navegando a editarPromocion/${promo.id}")
                        navController.navigate("editarPromocion/${promo.id}")
                    },
                    onDelete = { id -> pendingDeleteId = id }
                )


                if (index < promociones.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFFEAEAEA),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }

        // 🔹 Diálogo de confirmación para eliminar
        val toDelete = promociones.firstOrNull { it.id == pendingDeleteId }
        ConfirmacionEliminarDialog(
            visible = toDelete != null,
            mensaje = toDelete?.let { "¿Seguro que deseas eliminar \"${it.nombre}\"?" } ?: "",
            isDeleting = isDeleting,
            onDismiss = { pendingDeleteId = null },
            onConfirm = {
                toDelete?.let { promo ->
                    scope.launch {
                        viewModel.eliminarPromocion(promo.id) { _, mensaje ->
                            // ✅ Snackbar también debe lanzarse dentro de un scope
                            scope.launch {
                                snackbarHostState.showSnackbar(message = mensaje)
                            }
                        }
                    }
                }
            }
        )




    }
}
/**
 * Item individual de la lista de promociones.
 *
 * Cada item muestra:
 * - Imagen de la promoción (placeholder si no hay imagen).
 * - Nombre y descripción de la promoción.
 * - Botones para editar y eliminar la promoción.
 *
 * @param promo Datos de la promoción a mostrar.
 * @param onEdit Callback que se ejecuta al presionar el botón de editar.
 * @param onDelete Callback que se ejecuta al presionar el botón de eliminar.
 * @param modifier Modificador opcional para personalizar el layout.
 */
@Composable
private fun PromoListItem(
    promo: Promocion,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🖼️ Imagen
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF4F4F4)),
            contentAlignment = Alignment.Center
        ) {
            if (!promo.imagenUrl.isNullOrBlank()) {
                AsyncImage(
                    model = promo.imagenUrl,
                    contentDescription = promo.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = promo.nombre,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Black,
                    fontSize = 18.sp
                )
            )
            Text(
                text = promo.descripcion ?: "Sin descripción",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            )
        }

        IconButton(onClick = { onEdit(promo.id) }) {
            Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = Color.Black)
        }
        IconButton(onClick = { onDelete(promo.id) }) {
            Icon(Icons.Outlined.DeleteOutline, contentDescription = "Eliminar", tint = Color.Black)
        }
    }
}
/**
 * Diálogo de confirmación para eliminar una promoción.
 *
 * Funcionalidades:
 * - Muestra el mensaje de confirmación.
 * - Permite cancelar la operación con "No".
 * - Permite confirmar la eliminación con "Sí".
 * - Muestra un CircularProgressIndicator si se está eliminando.
 *
 * @param visible Boolean que controla la visibilidad del diálogo.
 * @param mensaje Texto del mensaje de confirmación.
 * @param isDeleting Boolean que indica si la operación de eliminación está en curso.
 * @param onConfirm Callback que se ejecuta al confirmar la eliminación.
 * @param onDismiss Callback que se ejecuta al cancelar o cerrar el diálogo.
 */
@Composable
fun ConfirmacionEliminarDialog(
    visible: Boolean,
    mensaje: String,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    // 🎨 Colores exactos del popup de cerrar sesión
    val morado = Color(0xFF9605F7)         // Morado principal
    val moradoFondo = Color(0xFFE7C6FF)    // Fondo pastel morado
    val textoNegro = Color(0xFF000000)     // Texto principal

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 4.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 280.dp, max = 360.dp)
                    .padding(bottom = 16.dp)
            ) {
                // 🔹 Franja superior (encabezado) morado pastel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(moradoFondo),
                    contentAlignment = Alignment.Center
                ) {

                }

                Spacer(Modifier.height(12.dp))

                // 🔹 Texto del mensaje
                Text(
                    text = if (isDeleting) "Eliminando promoción..." else mensaje,
                    color = textoNegro,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // 🔹 Indicador de carga (si está eliminando)
                if (isDeleting) {
                    CircularProgressIndicator(
                        color = morado,
                        strokeWidth = 3.dp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(35.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // 🔹 Botones "No" y "Sí"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    // Botón "No" (bordeado)
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isDeleting,
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(1.dp, morado),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = morado
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "No",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Botón "Sí" (relleno morado pastel)
                    Button(
                        onClick = {
                            if (!isDeleting) onConfirm()
                        },
                        enabled = !isDeleting,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = moradoFondo,
                            contentColor = morado
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Sí",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}