package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

/**
 * Pantalla que muestra la lista de notificaciones del negocio.
 *
 * Presenta notificaciones sobre escaneos, promociones y actividades del negocio
 * con el tiempo transcurrido desde la notificación.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas
 */
@Composable
fun NotificacionesNegocioPage(navController: NavController) {
    Scaffold(
        topBar = { ArrowTopBar(navController, "Notificaciones") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Lista de notificaciones del negocio
            NotificacionNegocioItem(
                titulo = "Promoción exitosa",
                descripcion = "Se ha canjeado 15 veces tu promoción '2x1 en cafés'",
                tiempo = "Hace 4 min"
            )
            NotificacionNegocioItem(
                titulo = "Promoción exitosa",
                descripcion = "Se ha canjeado 8 veces tu promoción 'Descuento del 20%'",
                tiempo = "Hace 15 min"
            )
            NotificacionNegocioItem(
                titulo = "Promoción por expirar",
                descripcion = "Tu '3x2 en pizzas' expirará en 2 días",
                tiempo = "Hace 1 hora"
            )
            NotificacionNegocioItem(
                titulo = "Promoción por expirar",
                descripcion = "Tu 'Descuento en postres' expirará en 1 día",
                tiempo = "Hace 3 horas"
            )
            NotificacionNegocioItem(
                titulo = "Promoción exitosa",
                descripcion = "Se ha canjeado 25 veces tu promoción 'Combo familiar'",
                tiempo = "Hace 1 día"
            )
            NotificacionNegocioItem(
                titulo = "Promoción editada",
                descripcion = "Has modificado la promoción '3x2 en hamburguesas'",
                tiempo = "Hace 2 días"
            )
            NotificacionNegocioItem(
                titulo = "Promoción exitosa",
                descripcion = "Se ha canjeado 12 veces tu promoción 'Menú ejecutivo'",
                tiempo = "Hace 3 días"
            )
        }
    }
}

/**
 * Componente que representa un item individual de notificación para negocio.
 *
 * Muestra información sobre actividades del negocio con el tiempo transcurrido.
 *
 * @param titulo Título de la notificación
 * @param descripcion Descripción detallada de la notificación
 * @param tiempo Tiempo transcurrido desde la notificación
 */
@Composable
fun NotificacionNegocioItem(titulo: String, descripcion: String, tiempo: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(106.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cuadro de texto sin emoji
            CuadroTextoNotificacion(titulo)

            Spacer(modifier = Modifier.width(16.dp))

            // Contenido de la notificación
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = descripcion,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Texto del tiempo (en lugar del badge de color)
            Text(
                text = tiempo,
                fontSize = 12.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .width(80.dp)
            )
        }
    }
}

/**
 * Componente que muestra un cuadro de texto para las notificaciones del negocio.
 * Versión sin emojis, solo muestra texto abreviado.
 */
@Composable
fun CuadroTextoNotificacion(titulo: String) {
    Card(
        modifier = Modifier
            .size(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            // Texto abreviado según el tipo de notificación
            val textoCuadro = when {
                titulo.contains("éxito") -> "Éxito"
                titulo.contains("expirar") -> "Expira"
                titulo.contains("editada") -> "Editada"
                else -> "Notif"
            }

            Text(
                text = textoCuadro,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Versión alternativa con colores según el tipo de notificación
 */
@Composable
fun CuadroTextoNotificacionConColor(titulo: String) {
    Card(
        modifier = Modifier
            .size(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        val (textoCuadro, colorFondo, colorTexto) = when {
            titulo.contains("éxito") -> Triple(
                "Éxito",
                Color(0xFFE8F5E8),
                Color(0xFF2E7D32)
            )
            titulo.contains("expirar") -> Triple(
                "Expira",
                Color(0xFFFFF8E1),
                Color(0xFFF57C00)
            )
            titulo.contains("editada") -> Triple(
                "Editada",
                Color(0xFFE3F2FD),
                Color(0xFF1976D2)
            )
            else -> Triple(
                "Notif",
                Color(0xFFF5F5F5),
                Color(0xFF666666)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = textoCuadro,
                fontSize = 14.sp,
                color = colorTexto,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Previsualización de la pantalla de notificaciones del negocio.
 */
@Preview(showBackground = true)
@Composable
fun NotificacionesNegocioPreview() {
    val navController = rememberNavController()
    NotificacionesNegocioPage(navController)
}

/**
 * Previsualización de un item de notificación del negocio.
 */
@Preview(showBackground = true)
@Composable
fun NotificacionNegocioItemPreview() {
    NotificacionNegocioItem(
        titulo = "Promoción exitosa",
        descripcion = "Se ha canjeado 15 veces tu promoción '2x1 en cafés'",
        tiempo = "Hace 4 min"
    )
}

/**
 * Previsualización con cuadro de color
 */
@Preview(showBackground = true)
@Composable
fun NotificacionNegocioItemConColorPreview() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(106.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CuadroTextoNotificacionConColor("Promoción exitosa")

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Promoción exitosa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Se ha canjeado 15 veces tu promoción '2x1 en cafés'",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Hace 4 min",
                fontSize = 12.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier.width(80.dp)
            )
        }
    }
}