package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.viewmodel.QRViewModel

/**
 * Pantalla que muestra los detalles de un negocio específico y sus promociones activas.
 *
 * Esta pantalla presenta información detallada de un establecimiento comercial incluyendo:
 * - Imagen principal del negocio
 * - Nombre del establecimiento
 * - Ubicación con opción para ver en mapa
 * - Lista de promociones activas disponibles
 *
 * Utiliza un diseño scrollable con [LazyColumn] para mostrar la información de manera
 * organizada y eficiente, especialmente para listas largas de promociones.
 *
 * @param navController Controlador de navegación utilizado para manejar transiciones a otras pantallas
 *
 * @see PromocionItem Componente reutilizable que representa cada promoción individual
 * @see Pantalla.RUTA_MAPA_APP Ruta de navegación para la pantalla del mapa
 */

@Composable
fun NegocioDetallePage(
    navController: NavController,
    viewModel: QRViewModel // ← Recibe el ViewModel como parámetro
) {
    val moradoBoton = Color(0xFFE2C8FF)
    val moradoTexto = Color(0xFF9605F7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267",
                    contentDescription = "Imagen del negocio",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Establecimiento 1",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { navController.navigate(Pantalla.RUTA_MAPA_APP) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = moradoBoton
                    ),
                    shape = RoundedCornerShape(50.dp),
                    contentPadding = PaddingValues(horizontal = 25.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Ver en el mapa",
                        color = moradoTexto,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "Promociones activas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Lista de promociones
            items(4) { index ->
                val promocionData = when (index) {
                    0 -> PromocionItemData(
                        imagen = "https://images.unsplash.com/photo-1522336572468-97b06e8ef143",
                        titulo = "Cupón 1 - 20% descuento",
                        descripcion = "20% de descuento en compra total"
                    )
                    1 -> PromocionItemData(
                        imagen = "https://images.unsplash.com/photo-1606851092831-0c5d2a909b3a",
                        titulo = "Cupón 2 - 2x1 en bebidas",
                        descripcion = "2x1 en todas las bebidas"
                    )
                    2 -> PromocionItemData(
                        imagen = "https://images.unsplash.com/photo-1601924582971-6e1b8a8f4b02",
                        titulo = "Cupón 3 - Envío gratis",
                        descripcion = "Envío gratis en pedidos > $100"
                    )
                    else -> PromocionItemData(
                        imagen = "https://images.unsplash.com/photo-1616628182509-5d6f0c8857e5",
                        titulo = "Cupón 4 - Producto gratis",
                        descripcion = "Producto gratis con tu compra"
                    )
                }

                PromocionItem(
                    imagen = promocionData.imagen,
                    titulo = promocionData.titulo,
                    descripcion = promocionData.descripcion,
                    onAplicarClick = {
                        // Usar el ViewModel para aplicar la promoción
                        viewModel.aplicarPromocion(promocionData.titulo)
                        // Navegar a la pantalla QR
                        navController.navigate(Pantalla.RUTA_QR_PROMOCION)
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// Data class simple para las promociones
data class PromocionItemData(
    val imagen: String,
    val titulo: String,
    val descripcion: String
)

/**
 * Componente que representa un item individual de promoción en la lista del negocio.
 *
 * Muestra la información básica de cada promoción incluyendo imagen, título, descripción
 * y un botón para aplicar la promoción. Utiliza un diseño horizontal con imagen a la izquierda
 * y contenido textual junto al botón de acción a la derecha.
 *
 * @param imagen URL de la imagen representativa de la promoción
 * @param titulo Nombre o título de la promoción
 * @param descripcion Texto descriptivo breve de la promoción
 *
 * @see AsyncImage Componente para carga asincrónica de imágenes desde URL
 */
@Composable
fun PromocionItem(
    imagen: String,
    titulo: String,
    descripcion: String,
    onAplicarClick: () -> Unit
) {
    val moradoBoton = Color(0xFFE2C8FF)
    val moradoTexto = Color(0xFF9605F7)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imagen,
            contentDescription = titulo,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = descripcion,
                fontSize = 15.sp,
                color = Color(0xFF8B8B8B)
            )
        }

        Button(
            onClick = onAplicarClick,
            colors = ButtonDefaults.buttonColors(containerColor = moradoBoton),
            shape = RoundedCornerShape(40.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Aplicar",
                color = moradoTexto,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Función de preview para visualizar el diseño de la pantalla de detalle de negocio en Android Studio.
 *
 * Permite ver la interfaz completa con la imagen del negocio, información básica y lista
 * de promociones en un contexto aislado durante el desarrollo.
 *
 * @param showBackground Muestra el fondo blanco para mejor visualización
 * @param showSystemUi Muestra la barra de estado y navegación del sistema
 *
 * @see Preview Anotación que habilita la visualización en el panel de diseño de Android Studio
 */
