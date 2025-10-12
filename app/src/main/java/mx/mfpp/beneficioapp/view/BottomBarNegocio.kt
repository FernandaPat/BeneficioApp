package mx.mfpp.beneficioapp.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import mx.mfpp.beneficioapp.viewmodel.BeneficioJovenVM

/**
 * Componente de navegación inferior personalizado para la sección de negocio de la aplicación.
 *
 * Muestra un conjunto de iconos de navegación con animaciones de escala cuando están seleccionados
 * y maneja la navegación entre las diferentes pantallas del flujo de negocio.
 *
 * Este componente utiliza [NavigationBar] de Material Design 3 y proporciona retroalimentación
 * visual mediante animaciones de escala en los iconos seleccionados.
 *
 * @param navController Controlador de navegación utilizado para manejar las transiciones entre pantallas
 *
 * @see NavigationBar Componente de Material Design 3 para barras de navegación inferiores
 * @see NavigationBarItem Elementos individuales de la barra de navegación
 * @see Pantalla.listaPantallasNegocio Lista de pantallas disponibles para navegación
 *
 * @sample Se puede previsualizar en conjunto con otras pantallas que utilicen este componente
 *
 * @example
 * ```
 * // Uso típico en un Scaffold
 * Scaffold(
 *     bottomBar = { BottomNavigationNegocio(navController) }
 * ) { contentPadding ->
 *     // Contenido de la pantalla
 * }
 * ```
 */
@Composable
fun BottomNavigationNegocio(navController: NavController) {
    /**
     * Estado actual de la pila de navegación.
     *
     * Se utiliza para determinar qué pantalla está actualmente activa y aplicar
     * los estilos correspondientes al item de navegación seleccionado.
     */
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    /**
     * Ruta actual del destino en la navegación.
     *
     * Se obtiene del back stack entry para comparar con las rutas de las pantallas
     * y determinar cuál item debe mostrarse como seleccionado.
     */
    val currentDestination = currentBackStackEntry?.destination?.route

    /**
     * Barra de navegación inferior que contiene los items de navegación.
     *
     * Se configura con dimensiones específicas y color de fondo blanco para
     * mantener consistencia con el diseño de la aplicación.
     */
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(87.dp), // Altura fija para mantener consistencia visual
        containerColor = Color.White // Fondo blanco para contraste
    ) {
        /**
         * Itera sobre cada pantalla definida en la lista de pantallas de negocio
         * y crea un item de navegación correspondiente.
         */
        Pantalla.listaPantallasNegocio.forEach { pantalla ->
            /**
             * Determina si esta pantalla es la actualmente seleccionada
             * comparando la ruta actual con la ruta de la pantalla.
             */
            val isSelected = currentDestination == pantalla.ruta

            /**
             * Animación de escala para el icono cuando está seleccionado.
             *
             * El icono seleccionado se escala a 1.3x su tamaño normal, proporcionando
             * retroalimentación visual clara al usuario sobre la pantalla activa.
             *
             * @see animateFloatAsState Animación que suaviza la transición de escala
             */
            val escala by animateFloatAsState(
                targetValue = if (isSelected) 1.3f else 1f,
                label = "escalaAnimacion" // Etiqueta para herramientas de desarrollo
            )

            /**
             * Item individual de la barra de navegación.
             *
             * Cada item representa una pantalla en la aplicación y maneja la navegación
             * hacia esa pantalla cuando es presionado.
             */
            NavigationBarItem(
                /**
                 * Estado de selección del item.
                 *
                 * Controla los colores y animaciones aplicadas al item.
                 */
                selected = isSelected,

                /**
                 * Callback ejecutado cuando el usuario presiona el item.
                 *
                 * Navega a la pantalla correspondiente si no está ya seleccionada,
                 * utilizando una estrategia de navegación que mantiene el estado.
                 */
                onClick = {
                    if (!isSelected) {
                        navController.navigate(pantalla.ruta) {
                            // Configuración de la estrategia de navegación
                            popUpTo(Pantalla.RUTA_INICIO_NEGOCIO) {
                                saveState = true // Preserva el estado de la pantalla
                            }
                            launchSingleTop = true // Evita múltiples instancias
                            restoreState = true // Restaura el estado previo si existe
                        }
                    }
                },

                /**
                 * Icono del item de navegación.
                 *
                 * Utiliza un recurso drawable y aplica la animación de escala
                 * cuando el item está seleccionado.
                 */
                icon = {
                    Icon(
                        painter = painterResource(id = pantalla.iconoResId),
                        contentDescription = pantalla.etiqueta,
                        modifier = Modifier
                            .size(26.dp) // Tamaño base del icono
                            .scale(escala), // Aplica la animación de escala
                        tint = if (isSelected) Color(0xFF9605F7) else Color.Gray // Color condicional
                    )
                },

                /**
                 * Etiqueta de texto del item.
                 *
                 * Muestra el nombre de la pantalla con estilos que cambian
                 * según el estado de selección.
                 */
                label = {
                    Text(
                        text = pantalla.etiqueta,
                        fontSize = 11.sp, // Tamaño reducido para mejor ajuste
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color(0xFF9605F7) else Color.Gray,
                        maxLines = 1, // Evita desbordamiento de texto
                        overflow = TextOverflow.Ellipsis // Trunca texto largo con puntos suspensivos
                    )
                },

                /**
                 * Configuración para mostrar siempre la etiqueta.
                 *
                 * A diferencia del comportamiento por defecto que oculta etiquetas
                 * en algunos estados, esta barra siempre muestra los textos.
                 */
                alwaysShowLabel = true,

                /**
                 * Colores personalizados para los diferentes estados del item.
                 *
                 * Define la paleta de colores para estados seleccionado y no seleccionado,
                 * utilizando morado (#9605F7) para selección y gris para estado normal.
                 */
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF9605F7),    // Morado para icono seleccionado
                    unselectedIconColor = Color.Gray,         // Gris para icono no seleccionado
                    selectedTextColor = Color(0xFF9605F7),    // Morado para texto seleccionado
                    unselectedTextColor = Color.Gray,         // Gris para texto no seleccionado
                    indicatorColor = Color.Transparent        // Sin color de indicador (usa animación de escala)
                )
            )
        }
    }
}