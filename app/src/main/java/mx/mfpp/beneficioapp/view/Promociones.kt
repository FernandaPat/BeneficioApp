package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.viewmodel.PromocionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Promociones(
    navController: NavController,
    viewModel: PromocionesViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // ✅ Recolecta los datos reales desde la API
    val promociones by viewModel.promociones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var pendingDeleteId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Promociones registradas",
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF9605F7))
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = error ?: "Error desconocido",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }

            promociones.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay promociones registradas.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 24.dp)
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
            }
        }

        // 🔹 Diálogo de confirmación
        val toDelete = promociones.firstOrNull { it.id == pendingDeleteId }
        ConfirmacionEliminarDialog(
            visible = toDelete != null,
            mensaje = toDelete?.let { "¿Seguro que deseas eliminar \"${it.nombre}\"?" } ?: "",
            onDismiss = { pendingDeleteId = null },
            onConfirm = {
                toDelete?.let {
                    scope.launch {
                        viewModel.eliminarPromocion(it.id)
                    }
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
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFF3FF)),
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

    val accent = Color(0xFF9605F7)

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
                Text(
                    text = mensaje,
                    color = Color.Black,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = accent),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("No")
                    }
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent.copy(alpha = 0.2f),
                            contentColor = accent
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
