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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.model.Promocion

@Composable
fun Promociones(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // BACKEND
    val promos = remember {
        mutableStateListOf(
            Promocion(1, "Ropa 10% de descuento", "https://picsum.photos/seed/1/300", "10%", "Comida", 3, "Atizapán", false, 4.5, "Cupon ropa 10%"),
            Promocion(2, "Promoción 2", "https://picsum.photos/seed/2/300", "15%", "Entretenimiento", 0, "CDMX", true, 3.8, "Descripción"),
            Promocion(3, "Promoción 3", null, "20%", "Cine", 5, "CDMX", false, null, "Descripción")
        )
    }
    var pendingDeleteId by remember { mutableStateOf<Int?>(null) }
    Scaffold(
        topBar = {
            ArrowTopBarNegocio(
                navController = navController,
                text = "Promociones",
                showAdd = true,
                onAddClick = { navController.navigate(Pantalla.RUTA_AGREGAR_PROMOCIONES) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            itemsIndexed(promos, key = { _, p -> p.id }) { index, promo ->
                PromoListItem(
                    promo = promo,
                    onEdit = { navController.navigate(Pantalla.RUTA_EDITAR_PROMOCIONES)
                    },
                    onDelete = { id ->
                        pendingDeleteId = id // abrir confirmación
                    }
                )
                if (index < promos.lastIndex) {
                    Divider(
                        color = Color(0xFFEAEAEA),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }

        // Mostrar diálogo estilo personalizado cuando haya algo por borrar
        val toDelete = promos.firstOrNull { it.id == pendingDeleteId }
        ConfirmacionEliminarDialog(
            visible = toDelete != null,
            mensaje = toDelete?.let { "¿Seguro que deseas eliminar \"${it.nombre}\"?" } ?: "",
            onDismiss = { pendingDeleteId = null },
            onConfirm = {
                toDelete?.let { promos.remove(it) } // eliminar del listado
                pendingDeleteId = null
            }
        )
    }
}

/* ---------- Item de la lista (imagen + textos + botones a la derecha) ---------- */

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
        // Imagen izquierda (64dp, esquinas redondeadas)
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

        // Título + descripción
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
                    color = Color(0xFF9E9E9E),
                    fontSize = 15.sp
                )
            )
        }

        // Botones (editar / eliminar)
        IconButton(onClick = { onEdit(promo.id) }) {
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Editar", tint = Color.Black)
        }
        IconButton(onClick = { onDelete(promo.id) }) {
            Icon(imageVector = Icons.Outlined.DeleteOutline, contentDescription = "Eliminar", tint = Color.Black)
        }
    }
}

/* ---------- Diálogo personalizado “¿Seguro que deseas eliminar?” ---------- */

@Composable
fun ConfirmacionEliminarDialog(
    visible: Boolean,
    mensaje: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    // Cambia este color si quieres usar tu rosa #FF00A1

    val accent = Color(0xFF9605f7)

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
                // Franja superior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(accent.copy(alpha = 0.25f))
                )

                Spacer(Modifier.height(16.dp))

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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    // Botón "No" (Outlined pill)
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = accent
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("No")
                    }

                    // Botón "Sí" (pill con fondo suave)
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent.copy(alpha = 0.25f),
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

/* ---------- Preview ---------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PromocionesPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        Promociones(navController)
    }
}
