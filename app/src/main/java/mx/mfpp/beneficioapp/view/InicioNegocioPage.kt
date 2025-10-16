package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import mx.mfpp.beneficioapp.R
import mx.mfpp.beneficioapp.viewmodel.ScannerViewModel

/**
 * Pantalla principal del flujo de negocio que sirve como dashboard para las operaciones del negocio.
 *
 * Esta pantalla proporciona una interfaz centralizada donde los negocios pueden:
 * - Acceder a su perfil
 * - Ver notificaciones
 * - Escanear códigos QR de clientes
 * - Gestionar promociones
 * - Ver promociones recientes
 *
 * La pantalla utiliza un diseño [Scaffold] con [LazyColumn] para mostrar contenido scrollable
 * y maneja la navegación a diferentes secciones de la aplicación.
 *
 * @param navController Controlador de navegación para manejar transiciones entre pantallas
 *
 * @see ScannerViewModel ViewModel que gestiona el estado del scanner para negocios
 * @see Pantalla Definiciones de rutas y configuración de navegación
 *
 * @sample InicioNegocioPreview Función de preview para Android Studio
 *
 * @example
 * ```
 * // En un NavGraph
 * composable(Pantalla.RUTA_INICIO_NEGOCIO) {
 *     InicioNegocioPage(navController)
 * }
 * ```
 */
@Composable
fun InicioNegocioPage(navController: NavController) {
    /**
     * Paleta de colores personalizada para la interfaz de negocio.
     *
     * Utiliza tonos morados consistentes con la identidad visual de la aplicación.
     */
    val moradoBoton = Color(0xFFE2C8FF)  // Color de fondo para botones
    val moradoTexto = Color(0xFF9605F7)  // Color principal para texto e iconos

    /**
     * ViewModel que gestiona el estado del scanner para el flujo de negocio.
     *
     * Se inicializa aquí para asegurar que esté disponible para todas las operaciones
     * que requieran gestión del scanner, como resetear el estado del scanner.
     */
    val scannerViewModel: ScannerViewModel = viewModel()

    /**
     * Estructura principal de la pantalla usando Material Design 3.
     *
     * Proporciona un contenedor base con color de fondo blanco y maneja el padding
     * del sistema (notch, barras de estado, etc.).
     */
    Scaffold(
        containerColor = Color.White  // Fondo blanco para toda la pantalla
    ) { innerPadding ->
        /**
         * Columna perezosa que renderiza eficientemente el contenido scrollable.
         *
         * Organiza los elementos en una columna con alineación centrada horizontalmente
         * y maneja el padding interno del Scaffold.
         */
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)  // Respeta el padding del sistema
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally  // Centra contenido horizontalmente
        ) {
            /**
             * Sección de encabezado y controles principales.
             *
             * Contiene el perfil del negocio, nombre y botón de notificaciones.
             */
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Encabezado con perfil y notificaciones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween  // Distribuye espacio entre elementos
                ) {
                    // Sección izquierda: Perfil del negocio
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        /**
                         * Botón de perfil que navega a la pantalla de edición de perfil.
                         *
                         * Utiliza un icono de cuenta con tamaño reducido para mejor proporción.
                         */
                        IconButton(
                            onClick = {
                                navController.navigate(Pantalla.RUTA_PERFIL_NEGOCIO)
                            },
                            modifier = Modifier.size(60.dp)  // Tamaño consistente con diseño
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.size(60.dp),  // Icono de perfil
                                tint = Color.LightGray  // Color neutral para estado por defecto
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        /**
                         * Nombre del negocio.
                         *
                         * @todo En implementación real, debería venir de datos del usuario/negocio
                         */
                        Text(
                            text = "NombreNegocio",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }

                    /**
                     * Botón de notificaciones que navega a la pantalla de notificaciones.
                     *
                     * Utiliza un icono de campana personalizado desde recursos drawable.
                     */
                    IconButton(onClick = {
                        navController.navigate(Pantalla.RUTA_NOTIFICACIONES_APP)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.bell),
                            contentDescription = "Notificaciones",
                            tint = Color.Gray  // Color neutral para el icono
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                /**
                 * Botón principal para escanear códigos QR.
                 *
                 * Realiza dos acciones importantes:
                 * 1. Resetea el estado del scanner en el ViewModel
                 * 2. Navega a la pantalla de scanner
                 *
                 * @see ScannerViewModel.resetScannerState Método que prepara el scanner para nuevo uso
                 */
                Button(
                    onClick = {
                        // Resetear el estado del scanner antes de navegar
                        scannerViewModel.resetScannerState()  // Prepara el scanner para nuevo uso
                        navController.navigate(Pantalla.RUTA_SCANER_NEGOCIO) {
                            launchSingleTop = true  // Evita múltiples instancias
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .height(50.dp),  // Altura consistente para botones
                    colors = ButtonDefaults.buttonColors(containerColor = moradoBoton),  // Fondo morado claro
                    shape = RoundedCornerShape(20.dp)  // Esquinas redondeadas
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Escanear QR",
                        tint = moradoTexto  // Icono en morado principal
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Escanear QR",
                        color = moradoTexto,  // Texto en morado principal
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                /**
                 * Botón para agregar nuevas promociones.
                 *
                 * Navega directamente a la pantalla de creación de promociones.
                 */
                Button(
                    onClick = {
                        navController.navigate(Pantalla.RUTA_AGREGAR_PROMOCIONES)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = moradoBoton),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.gift),
                        contentDescription = "Agregar promoción",
                        tint = moradoTexto
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Agregar Promoción",
                        color = moradoTexto,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                /**
                 * Título de sección para promociones recientes.
                 *
                 * Separa visualmente los botones de acción de la lista de contenido.
                 */
                Text(
                    text = "Promociones recientes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            /**
             * Lista de promociones recientes del negocio.
             *
             * Actualmente muestra datos de ejemplo (3 promociones) con imágenes de placeholder
             * desde picsum.photos. En implementación real, estos datos vendrían del ViewModel.
             *
             * Cada item es clickable y navega a la pantalla de edición de promociones.
             */
            items(listOf(1, 2, 3)) { index ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp, vertical = 10.dp)
                        .clickable {
                            // Navega a edición cuando se presiona una promoción
                            navController.navigate(Pantalla.RUTA_EDITAR_PROMOCIONES)
                        }
                ) {
                    /**
                     * Imagen de la promoción cargada asincrónicamente.
                     *
                     * Utiliza Coil para cargar imágenes de internet con efecto crossfade
                     * y placeholder de picsum.photos para demostración.
                     */
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://picsum.photos/seed/${index + 1}/300")  // Imagen única por índice
                            .crossfade(true)  // Efecto de transición suave
                            .build(),
                        contentDescription = "Promoción",
                        contentScale = ContentScale.Crop,  // Recorta la imagen para ajustarse
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))  // Esquinas redondeadas
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Información textual de la promoción
                    Text(
                        text = "Promoción ${index + 1}",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Descripción de la promoción ${index + 1}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall  // Texto más pequeño para descripción
                    )
                }
            }

            /**
             * Espacio final para evitar que el contenido quede oculto tras la barra de navegación.
             */
            item {
                Spacer(modifier = Modifier.height(70.dp))  // Espacio para bottom navigation
            }
        }
    }
}

/**
 * Función de preview para visualizar el diseño de la pantalla de inicio de negocio en Android Studio.
 *
 * Permite ver la interfaz de usuario sin necesidad de ejecutar la aplicación completa,
 * mostrando todos los elementos en un contexto aislado.
 *
 * @see Preview Anotación que habilita la visualización en el panel de diseño
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioNegocioPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        InicioNegocioPage(navController)
    }
}