package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.viewmodel.PromocionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Promociones(
    navController: NavController,
    viewModel: PromocionesViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val promociones by viewModel.promociones.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        val idNegocio = sessionManager.getNegocioId() ?: 0
        if (idNegocio != 0) {
            viewModel.cargarPromociones(idNegocio)
        }
    }


    Scaffold(
        topBar = { ArrowTopBar(navController, "Promociones") },
        snackbarHost = { SnackbarHost(snackbarHostState)
        },

        // 💜 Botón flotante para agregar nuevas promociones
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Pantalla.RUTA_AGREGAR_PROMOCIONES) },
                containerColor = Color(0xFF9605F7),
                contentColor = Color.White,
                shape = RoundedCornerShape(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar promoción"
                )
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
                    onEdit = { navController.navigate("editarPromocion/${promo.id}") },
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
            onDismiss = { pendingDeleteId = null },
            onConfirm = {
                toDelete?.let {
                    scope.launch { viewModel.eliminarPromocion(it.id) }
                }
                pendingDeleteId = null
            }
        )
    }
}

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
        // 🖼️ Imagen de promoción (o contenedor vacío si no hay)
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
                text = promo.descripcion ?: "Descripción",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            )
        }

        IconButton(onClick = { onEdit(promo.id) }) {
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Editar", tint = Color.Black)
        }
        IconButton(onClick = { onDelete(promo.id) }) {
            Icon(imageVector = Icons.Outlined.DeleteOutline, contentDescription = "Eliminar", tint = Color.Black)
        }
    }
}


@Composable
fun ConfirmacionEliminarDialog(
    visible: Boolean,
    mensaje: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val moradoTexto = Color(0xFF9605F7)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 2.dp,
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 280.dp, max = 360.dp)
                    .padding(bottom = 16.dp)
            ) {
                // 🔹 Encabezado decorativo morado (igual que el de cerrar sesión)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(moradoTexto.copy(alpha = 0.25f))
                )

                Spacer(Modifier.height(16.dp))

                // 🔹 Texto principal
                Text(
                    text = mensaje,
                    color = Color.Black,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // 🔹 Botones de acción
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = moradoTexto
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("No")
                    }
                    Button(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = moradoTexto.copy(alpha = 0.25f),
                            contentColor = moradoTexto
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sí")
                    }
                }
            }
        }
    }
}

