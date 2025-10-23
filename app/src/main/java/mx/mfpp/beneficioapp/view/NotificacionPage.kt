package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.mfpp.beneficioapp.data.local.NotificacionEntity
import mx.mfpp.beneficioapp.viewmodel.NotificacionesViewModel
import mx.mfpp.beneficioapp.viewmodel.NotificacionesViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de notificaciones con Room Database
 * Muestra notificaciones persistentes localmente
 */
@Composable
fun NotificacionPage(navController: NavController) {
    val context = LocalContext.current
    val viewModel: NotificacionesViewModel = viewModel(
        factory = NotificacionesViewModelFactory(context)
    )

    val notificaciones by viewModel.notificaciones.collectAsState()
    val contadorNoLeidas by viewModel.contadorNoLeidas.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ✅ Usar ArrowTopBar personalizado
        Box {
            ArrowTopBar(
                navController = navController,
                text = if (contadorNoLeidas > 0) {
                    "Notificaciones ($contadorNoLeidas)"
                } else {
                    "Notificaciones"
                }
            )

            // Menú de opciones en la esquina superior derecha
            if (notificaciones.isNotEmpty()) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        "Opciones",
                        tint = Color.Black
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (contadorNoLeidas > 0) {
                        DropdownMenuItem(
                            text = { Text("Marcar todas como leídas") },
                            onClick = {
                                viewModel.marcarTodasComoLeidas()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DoneAll, null)
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = { Text("Eliminar todas") },
                        onClick = {
                            viewModel.eliminarTodas()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.DeleteSweep, null)
                        }
                    )
                }
            }
        }

        // Contenido
        if (notificaciones.isEmpty()) {
            // Estado vacío
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Text(
                        "No tienes notificaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Text(
                        "Las notificaciones de promociones aparecerán aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Lista de notificaciones
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                items(
                    items = notificaciones,
                    key = { it.id }
                ) { notificacion ->
                    NotificacionItem(
                        notificacion = notificacion,
                        onClick = {
                            viewModel.marcarComoLeida(notificacion.id)
                        },
                        onDelete = {
                            viewModel.eliminarNotificacion(notificacion)
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun NotificacionItem(
    notificacion: NotificacionEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .background(
                    if (notificacion.leida) Color.White
                    else Color(0xFFF3E5F5) // Color morado claro para no leídas
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Indicador de no leída
            if (!notificacion.leida) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9605F7)) // Tu color morado
                        .align(Alignment.CenterVertically)
                )
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Ícono según el tipo
            Icon(
                imageVector = when (notificacion.tipo) {
                    "promocion" -> Icons.Default.LocalOffer
                    "favorito" -> Icons.Default.Favorite
                    else -> Icons.Default.Notifications
                },
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE1BEE7)) // Morado muy claro
                    .padding(8.dp),
                tint = Color(0xFF9605F7)
            )

            // Contenido
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notificacion.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notificacion.mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                if (notificacion.nombreEstablecimiento != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notificacion.nombreEstablecimiento,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9605F7),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatearTiempo(notificacion.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Menú de opciones
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, "Opciones", tint = Color.Gray)
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Eliminar") },
                onClick = {
                    onDelete()
                    showMenu = false
                },
                leadingIcon = { Icon(Icons.Default.Delete, null) }
            )
        }
    }
}

fun formatearTiempo(timestamp: Long): String {
    val ahora = System.currentTimeMillis()
    val diferencia = ahora - timestamp

    return when {
        diferencia < 60_000 -> "Ahora"
        diferencia < 3_600_000 -> "${diferencia / 60_000} min"
        diferencia < 86_400_000 -> "${diferencia / 3_600_000} h"
        diferencia < 604_800_000 -> "${diferencia / 86_400_000} días"
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}