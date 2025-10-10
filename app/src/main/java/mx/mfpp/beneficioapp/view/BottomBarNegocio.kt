package mx.mfpp.beneficioapp.view

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import mx.mfpp.beneficioapp.R

sealed class BottomBarNegocio(
    val ruta: String,
    val etiqueta: String,
    val iconoResId: Int
) {
    companion object {
        val listaPantallas = listOf(
            BeneficioNegocioINICIO,
            BeneficioNegocioPROMOS,
            BeneficioNegocioESCANER,
            BeneficioNegocioPERFIL
        )

        // 🔹 Rutas del negocio
        const val RUTA_INICIO_NEGOCIO = "InicioNegocioPage"
        const val RUTA_PROMOCIONES_NEGOCIO = "Promociones"
        const val RUTA_ESCANER_NEGOCIO = "EscanerPage"
        const val RUTA_PERFIL_NEGOCIO = "PerfilNegocioPage"
        const val RUTA_AGREGAR_PROMOCIONES = "Agregar_Promociones"
        const val RUTA_EDITAR_PROMOCIONES = "Editar_Promociones"
    }

    // 🔹 Definición de cada pantalla
    object BeneficioNegocioINICIO :
        BottomBarNegocio(RUTA_INICIO_NEGOCIO, "Inicio", R.drawable.home)

    object BeneficioNegocioPROMOS :
        BottomBarNegocio(RUTA_PROMOCIONES_NEGOCIO, "Promos", R.drawable.giftb)

    object BeneficioNegocioESCANER :
        BottomBarNegocio(RUTA_ESCANER_NEGOCIO, "Escáner", R.drawable.camerab)

    object BeneficioNegocioPERFIL :
        BottomBarNegocio(RUTA_PERFIL_NEGOCIO, "Perfil", R.drawable.userb)
}

@Composable
fun BottomNavigationNegocio(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        containerColor = Color.White
    ) {
        BottomBarNegocio.listaPantallas.forEach { pantalla ->
            val selected = currentDestination == pantalla.ruta

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(pantalla.ruta) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = pantalla.iconoResId),
                        contentDescription = pantalla.etiqueta,
                        tint = if (selected) Color(0xFF7B4EFF) else Color(0xFFBDBDBD)
                    )
                },
                label = {
                    Text(
                        text = pantalla.etiqueta,
                        color = if (selected) Color(0xFF7B4EFF) else Color(0xFFBDBDBD)
                    )
                }
            )
        }
    }
}


