package mx.mfpp.beneficioapp.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.random.Random

sealed class Pantalla(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
)
{
    companion object { // miembros estaticos

        var listaPantallas = listOf(BeneficioJovenINICIO,
                                    BeneficioJovenMAPA,
                                    BeneficioJovenTARJETA,
                                    BeneficioJovenBUSCAR,
                                    BeneficioJovenACTIVIDAD)
        const val RUTA_INICIO_APP = "InicioPage"
        const val RUTA_MAPA_APP = "MapaPage"
        const val RUTA_TARJETA_APP = "Tarjeta"
        const val RUTA_BUSCAR_APP = "Buscar"
        const val RUTA_ACTIVIDAD_APP = "Actividad"

    }

    data object BeneficioJovenINICIO:
            Pantalla(RUTA_INICIO_APP, "Inicio", Icons.Default.Home)
    data object BeneficioJovenMAPA:
            Pantalla(RUTA_MAPA_APP, "Mapa", Icons.Default.LocationOn) // Cambiar Icono a un Mapa

    data object BeneficioJovenTARJETA:
            Pantalla(RUTA_TARJETA_APP, "Tarjeta", Icons.Default.Email) // Cambiar Icono a una Tarjeta

    data object BeneficioJovenBUSCAR:
            Pantalla(RUTA_BUSCAR_APP, "Buscar", Icons.Default.Search)

    data object BeneficioJovenACTIVIDAD:
            Pantalla(RUTA_ACTIVIDAD_APP, "Actividad", Icons.Default.ShoppingCart) // Cambiar Icono a el de Figma
}

