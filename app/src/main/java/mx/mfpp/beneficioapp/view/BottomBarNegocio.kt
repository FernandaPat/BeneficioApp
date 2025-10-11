package mx.mfpp.beneficioapp.view

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.getValue

@Composable
fun BottomNavigationNegocio(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(87.dp),
        containerColor = Color.White
    ) {
        Pantalla.listaPantallasNegocio.forEach { pantalla ->
            val isSelected = currentDestination == pantalla.ruta

            val (modifierAnimado, colorAnimado) = crearAnimacionNavegacion(
                estaSeleccionado = isSelected,
                colorNormal = Color.Gray,
                colorActivado = Color(0xFF9605F7),
                escalaActivado = 1.3f
            )

            val gradientBrush = Brush.linearGradient(
                colors = listOf(Color(0xFF9605F7), Color(0xFFB150FF))
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(pantalla.ruta) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = pantalla.iconoResId),
                        contentDescription = pantalla.etiqueta,
                        modifier = Modifier
                            .size(26.dp)
                            .then(modifierAnimado),
                        tint = colorAnimado
                    )
                },
                label = {
                    Text(
                        text = pantalla.etiqueta,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        style = if (isSelected) {
                            androidx.compose.ui.text.TextStyle(
                                brush = gradientBrush,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            androidx.compose.ui.text.TextStyle(
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF9605F7),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFF9605F7),
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}