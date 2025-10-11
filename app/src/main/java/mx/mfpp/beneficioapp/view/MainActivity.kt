package mx.mfpp.beneficioapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
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

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
        Pantalla.RUTA_ESTATUS_SOLICITUD_APP,
        Pantalla.RUTA_JN_APP,
        Pantalla.RUTA_LOGIN_APP,
        Pantalla.RUTA_INICIAR_SESION,
        Pantalla.RUTA_CREAR_CUENTA,
        Pantalla.RUTA_INICIAR_SESION_NEGOCIO,
        Pantalla.RUTA_PROMOCIONES,
        Pantalla.RUTA_AGREGAR_PROMOCIONES,
        Pantalla.RUTA_EDITAR_PROMOCIONES,
        Pantalla.RUTA_SCANER_NEGOCIO
    )

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    val showBottomBar = currentRoute !in hiddenBottomBarRoutes

    val isNegocioRoute = currentRoute in listOf(
        Pantalla.RUTA_INICIO_NEGOCIO,
        Pantalla.RUTA_PROMOCIONES_NEGOCIO,
        Pantalla.RUTA_SCANER_NEGOCIO,
        Pantalla.RUTA_PERFIL_NEGOCIO,
        Pantalla.RUTA_INICIONEGOCIO_APP
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                if (isNegocioRoute) {
                    BottomNavigationNegocio(navController)
                } else {
                    AppBottomBar(navController)
                }
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
        startDestination = Pantalla.RUTA_JN_APP,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Pantalla.RUTA_INICIAR_SESION_NEGOCIO) {
            Iniciar_Sesion_Negocio(navController)
        }
        composable(Pantalla.RUTA_PROMOCIONES) {
            Promociones(navController)
        }
        composable(Pantalla.RUTA_JN_APP) {
            JNPage(navController)
        }
        composable(Pantalla.RUTA_PERFIL_APP) {
            PerfilPage(navController)
        }
        composable(Pantalla.RUTA_INICIONEGOCIO_APP) {
            InicioNegocioPage(navController)
        }
        composable(Pantalla.RUTA_LOGIN_APP) {
            LoginPage(navController)
        }
        composable(Pantalla.RUTA_AGREGAR_PROMOCIONES) {
            Agregregar_Promociones(navController)
        }
        composable(Pantalla.RUTA_EDITAR_PROMOCIONES) {
            Editar_Promociones(navController)
        }
        composable(Pantalla.RUTA_INICIAR_SESION) {
            Iniciar_Sesion(navController)
        }
        composable(Pantalla.RUTA_CREAR_CUENTA) {
            Crear_Cuenta(navController)
        }

        // Grafo de navegación Nav bar - JÓVENES
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
            ExplorarPage(navController)
        }
        composable(Pantalla.RUTA_ACTIVIDAD_APP) {
            //Agregar
        }

        // En AppNavHost - AGREGAR estas rutas
        composable(Pantalla.RUTA_BUSCAR_APP) {
            ExplorarPage(navController)
        }

        composable(Pantalla.RUTA_RESULTADOS_CON_CATEGORIA) { backStackEntry ->
            val categoria = backStackEntry.arguments?.getString("categoria")
            ResultadosPage(
                navController = navController,
                categoriaSeleccionada = categoria
            )
        }

        // También agregar una ruta simple para resultados sin categoría
        composable(Pantalla.RUTA_RESULTADOS_APP) {
            ResultadosPage(navController)
        }

        // Grafo de navegación Nav bar - NEGOCIO
        composable(Pantalla.RUTA_INICIO_NEGOCIO) {
            InicioNegocioPage(navController)
        }
        composable(Pantalla.RUTA_PROMOCIONES_NEGOCIO) {
            Promociones(navController)
        }

        composable(Pantalla.RUTA_SCANER_NEGOCIO) {
            val viewModel: BeneficioJovenVM = viewModel()
            val showScanner by viewModel.showScanner.collectAsState()

            if (showScanner) {
                ScannerQrScreen(
                    onQrScanned = { qrContent ->
                        viewModel.addQrScanResult(qrContent)
                    },
                    onBack = {
                        viewModel.hideScanner()
                    },
                    navController = navController // <- PASA el navController aquí
                )
            } else {
                HistorialScannerScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        composable(Pantalla.RUTA_PERFIL_NEGOCIO) {
            PerfilPage(navController)
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
                            .size(26.dp)
                            .then(modifierAnimado),
                        tint = colorAnimado
                    )
                },
                label = {
                    Text(
                        pantalla.etiqueta,
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