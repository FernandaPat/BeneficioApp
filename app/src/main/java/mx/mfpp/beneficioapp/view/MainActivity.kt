package mx.mfpp.beneficioapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.ui.theme.BeneficioAppTheme
import mx.mfpp.beneficioapp.viewmodel.BeneficioJovenVM

class MainActivity : ComponentActivity() {
    private val viewModel: BeneficioJovenVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar edge-to-edge y ocultar barra de navegación
        enableEdgeToEdge()

        // Ocultar la barra de navegación
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Hacer la barra de navegación transparente - CORREGIDO
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            BeneficioAppTheme {
                AppPrincipal(viewModel)
            }
        }
    }
}

@Composable
fun AppPrincipal(
    beneficioJovenVM: BeneficioJovenVM,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val hiddenBottomBarRoutes = listOf(
        Pantalla.RUTA_SOLICITUD_APP,
        Pantalla.RUTA_ESTATUS_SOLICITUD_APP
    )

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    val showBottomBar = currentRoute !in hiddenBottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(navController)
            }
        },
    ) { innerPadding ->
        AppNavHost(
            beneficioJovenVM = beneficioJovenVM,
            navController = navController,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AppNavHost(
    beneficioJovenVM: BeneficioJovenVM,
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Pantalla.RUTA_INICIO_APP,
        modifier = modifier.fillMaxSize()
    ) {
        // Grafo de navegación Nav bar
        composable(Pantalla.RUTA_INICIO_APP) {
            InicioPage(navController, beneficioJovenVM)
        }
        composable(Pantalla.RUTA_MAPA_APP) {
            MapaPage(navController)
        }
        composable(Pantalla.RUTA_TARJETA_APP) {
            TarjetaPage(navController)
        }
        composable(Pantalla.RUTA_BUSCAR_APP) {
            //Agregar
        }
        composable(Pantalla.RUTA_ACTIVIDAD_APP) {
            //Agregar
        }

        // Paginas fuera de la barra de navegacion
        composable(Pantalla.RUTA_SOLICITUD_APP) {
            SolicitudPage(navController)
        }
        composable(Pantalla.RUTA_ESTATUS_SOLICITUD_APP) {
            EstatusSolicitudPage(navController)
        }
        composable(Pantalla.RUTA_NOTIFICACIONES_APP) {
            NotificacionPage(navController)
        }
    }
}

@Composable
fun AppBottomBar(navController: NavHostController) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(87.dp),
        containerColor = Color.White
    ) {
        val pilaNavegacion by navController.currentBackStackEntryAsState()
        val pantallaActual = pilaNavegacion?.destination

        Pantalla.listaPantallas.forEach { pantalla ->
            val isSelected = pantallaActual?.route == pantalla.ruta

            // Cambiar colores para que funcionen con fondo blanco
            val (modifierAnimado, colorAnimado) = crearAnimacionNavegacion(
                estaSeleccionado = isSelected,
                colorNormal = Color.Gray,
                colorActivado = Color(0xFFFF2291),
                escalaActivado = 1.3f
            )

            val gradientBrush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF2291), Color(0xFFFF7867))
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(pantalla.ruta) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = pantalla.iconoResId),
                        contentDescription = pantalla.etiqueta,
                        modifier = Modifier
                            .size(30.dp)
                            .then(modifierAnimado),
                        tint = colorAnimado
                    )
                },
                label = {
                    Text(
                        pantalla.etiqueta,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        style = if (isSelected) {
                            androidx.compose.ui.text.TextStyle(brush = gradientBrush)
                        } else {
                            androidx.compose.ui.text.TextStyle(color = Color.Gray)
                        }
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF2291),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFFFF2291),
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}