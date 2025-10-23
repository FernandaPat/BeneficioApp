package mx.mfpp.beneficioapp.view

import NegocioDetallePage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.mfpp.beneficioapp.model.SessionManager
import mx.mfpp.beneficioapp.ui.theme.BeneficioAppTheme
import mx.mfpp.beneficioapp.viewmodel.BeneficioJovenVM
import mx.mfpp.beneficioapp.viewmodel.BusquedaViewModel
import mx.mfpp.beneficioapp.viewmodel.CategoriasViewModel
import mx.mfpp.beneficioapp.viewmodel.PromocionJovenViewModel
import mx.mfpp.beneficioapp.viewmodel.PromocionesViewModel
import mx.mfpp.beneficioapp.viewmodel.QRViewModel
import mx.mfpp.beneficioapp.viewmodel.ScannerViewModel
import java.net.URLDecoder
import kotlin.getValue

/**
 * Actividad principal de la aplicación Beneficio Joven.
 *
 * Configura la interfaz principal, maneja la navegación y controla
 * la visibilidad de las barras del sistema.
 */
class MainActivity : ComponentActivity() {
    private val categoriasViewModel: CategoriasViewModel by viewModels()
    private val promocionesViewModel: PromocionJovenViewModel by viewModels()
    private val busquedaViewModel: BusquedaViewModel by viewModels() // Este es importante
    private val scannerViewModel: ScannerViewModel by viewModels()
    private val qrViewModel: QRViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Configuracion para guardar la sesion
        val sessionManager = SessionManager(applicationContext)
        val accessToken = sessionManager.getAccessToken()
        val userType = sessionManager.getUserType()

        val startDestination = when {
            accessToken != null && userType == "joven" -> Pantalla.RUTA_INICIO_APP
            accessToken != null && userType == "establecimiento" -> Pantalla.RUTA_INICIO_NEGOCIO
            else -> Pantalla.RUTA_JN_APP
        }


        // Configurar interfaz full-screen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            BeneficioAppTheme {
                AppPrincipal(
                    startDestination = startDestination,
                    categoriasViewModel = categoriasViewModel,
                    promocionesViewModel = promocionesViewModel,
                    busquedaViewModel = busquedaViewModel, // Pasar este ViewModel
                    scannerViewModel = scannerViewModel,
                    qrViewModel = qrViewModel
                )
            }
        }
    }
}


/**
 * Componente principal que orquesta toda la aplicación.
 */
@Composable
fun AppPrincipal(
    startDestination: String,
    categoriasViewModel: CategoriasViewModel,
    promocionesViewModel: PromocionJovenViewModel,
    busquedaViewModel: BusquedaViewModel,
    scannerViewModel: ScannerViewModel,
    qrViewModel: QRViewModel,
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
        Pantalla.RUTA_AGREGAR_PROMOCIONES,
        Pantalla.RUTA_EDITAR_PROMOCIONES,
        Pantalla.RUTA_QR_PROMOCION,
        Pantalla.RUTA_ACERCADE_APP,
        Pantalla.RUTA_AYUDA_APP,
        Pantalla.RUTA_CAMBIARCONTRASENA_APP,
        Pantalla.RUTA_DATOSPERSONALES_APP,
        Pantalla.RUTA_RECUPERAR_CONTRASENA,
        Pantalla.RUTA_TERMINOS_CONDICIONES,
        Pantalla.RUTA_DATOSNEGOCIO_APP,
        Pantalla.RUTA_QR_SCANNER_SCREEN
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination
    val currentRoute = destination?.route

    val showBottomBar = hiddenBottomBarRoutes.none { route ->
        currentRoute?.startsWith(route.substringBefore("/{")) == true
    }

    val isNegocioRoute = when {
        currentRoute == null -> false
        currentRoute == Pantalla.RUTA_INICIO_NEGOCIO -> true // ✅ Ruta específica
        currentRoute.startsWith(Pantalla.RUTA_PROMOCIONES_NEGOCIO) -> true
        currentRoute.startsWith(Pantalla.RUTA_SCANER_NEGOCIO) -> true
        currentRoute.startsWith(Pantalla.RUTA_PERFIL_NEGOCIO) -> true
        currentRoute.startsWith(Pantalla.RUTA_NOTIFICACIONES_NEGOCIO) -> true
        currentRoute.startsWith(Pantalla.RUTA_INICIAR_SESION_NEGOCIO) -> true
        currentRoute.startsWith("editarPromocion/") -> true
        else -> false
    }

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
            startDestination = startDestination,
            categoriasViewModel = categoriasViewModel,
            promocionesViewModel = promocionesViewModel,
            busquedaViewModel = busquedaViewModel,
            scannerViewModel = scannerViewModel,
            qrViewModel = qrViewModel,
            navController = navController,
            modifier = modifier.padding(innerPadding)
        )
    }
}

/**
 * Host de navegación principal que define todas las rutas de la aplicación.
 */
@Composable
fun AppNavHost(
    startDestination: String,
    categoriasViewModel: CategoriasViewModel,
    promocionesViewModel: PromocionJovenViewModel, // CAMBIAR AQUÍ
    busquedaViewModel: BusquedaViewModel,
    scannerViewModel: ScannerViewModel,
    qrViewModel: QRViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {
        // Rutas de autenticación y negocio
        composable(Pantalla.RUTA_PROMOCIONES) {
            Promociones(navController)
        }
        composable(Pantalla.RUTA_JN_APP) {
            JNPage(navController)
        }
        composable(Pantalla.RUTA_PERFIL_APP) {
            PerfilPage(navController)
        }
        composable(Pantalla.RUTA_INICIO_NEGOCIO) {
            InicioNegocioPage(navController)
        }
        composable(Pantalla.RUTA_LOGIN_APP) {
            LoginPage(navController)
        }
        composable(Pantalla.RUTA_AGREGAR_PROMOCIONES) {
            AgregarPromocion(navController)
        }
        composable(
            route = "editarPromocion/{id}",
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            Editar_Promociones(navController, id)
        }
        composable(Pantalla.RUTA_ACERCADE_APP) {
            AcercaDePage(navController)
        }
        composable(Pantalla.RUTA_AYUDA_APP) {
            AyudaPage(navController)
        }

        composable(Pantalla.RUTA_TERMINOS_CONDICIONES){
            TerminosYCondicionesPage(navController)
        }
        composable(Pantalla.RUTA_DATOSPERSONALES_APP) {
            VerDatosPersonalesPage(navController)
        }

        composable(Pantalla.RUTA_INICIAR_SESION_NEGOCIO) {
            Iniciar_Sesion_Negocio(navController)
        }

        composable(Pantalla.RUTA_INICIAR_SESION) {
            Iniciar_Sesion(navController)
        }

        composable(Pantalla.RUTA_RECUPERAR_CONTRASENA) {
            RecuperarContrasena(navController)
        }

        composable(Pantalla.RUTA_CREAR_CUENTA) {
            Crear_Cuenta(navController)
        }

        composable(
            "qrPromocion/{idPromocion}",
        ) { backStackEntry ->
            val idPromocion = backStackEntry.arguments?.getString("idPromocion")?.toIntOrNull() ?: 0

            // Obtener el id del joven desde SessionManager
            val context = LocalContext.current
            val sessionManager = SessionManager(context)
            val idJoven = sessionManager.getJovenId()

            if (idJoven == null) {
                // Si no hay usuario logueado, mostrar error o redirigir al login
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                    // Opcional: navegar al login
                    // navController.navigate(Pantalla.RUTA_LOGIN_APP)
                }
                // Mientras tanto mostrar un mensaje
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Usuario no autenticado")
                }
            } else {
                QRPromocionPage(
                    navController = navController,
                    viewModel = qrViewModel,
                    idJoven = idJoven,
                    idPromocion = idPromocion
                )
            }
        }

        // Grafo de navegación Nav bar - JÓVENES
        composable(Pantalla.RUTA_INICIO_APP) {
            InicioPage(
                navController = navController,
                categoriasViewModel = categoriasViewModel,
                promocionesViewModel = promocionesViewModel,
                busquedaViewModel = busquedaViewModel
            )
        }

        composable(Pantalla.RUTA_NOTIFICACIONES_NEGOCIO) {
            NotificacionesNegocioPage(navController)
        }

        composable(Pantalla.RUTA_MAPA_APP) {
            MapaPage(navController)
        }
        composable(Pantalla.RUTA_TARJETA_APP) {
            TarjetaPage(navController)
        }
        composable(Pantalla.RUTA_BUSCAR_APP) {
            ExplorarPage(
                navController = navController,
                categoriasViewModel = categoriasViewModel,
                busquedaViewModel = busquedaViewModel
            )
        }
        composable(Pantalla.RUTA_ACTIVIDAD_APP) {
            ActividadPage(navController)
        }

        composable(Pantalla.RUTA_SCANER_NEGOCIO) {
            val context = LocalContext.current
            val sessionManager = SessionManager(context)
            val idEstablecimiento = sessionManager.getNegocioId() ?: 0

            QrScannerScreen(
                navController = navController,
                idEstablecimiento = idEstablecimiento,
                viewModel = scannerViewModel
            )
        }


        composable(
            route = "${Pantalla.RUTA_DETALLEPROMOCION_APP}/{qrData}",
            arguments = listOf(navArgument("qrData") { type = NavType.StringType })
        ) { backStackEntry ->
            val qrData = backStackEntry.arguments?.getString("qrData")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: ""

            DetallePromocionScreen(
                navController = navController,
                qrData = qrData
            )
        }

        // Rutas de búsqueda y resultados
        composable(Pantalla.RUTA_RESULTADOS_CON_CATEGORIA) { backStackEntry ->
            val categoria = backStackEntry.arguments?.getString("categoria")
            ResultadosPage(
                navController = navController,
                categoriaSeleccionada = categoria,
                categoriasViewModel = categoriasViewModel,
                busquedaViewModel = busquedaViewModel
            )
        }

        composable(
            route = "${Pantalla.RUTA_RESULTADOS_APP}/{categoriaSeleccionada}",
            arguments = listOf(navArgument("categoriaSeleccionada") { type = NavType.StringType })
        ) { backStackEntry ->
            ResultadosPage(
                navController = navController,
                categoriaSeleccionada = backStackEntry.arguments?.getString("categoriaSeleccionada"),
                categoriasViewModel = categoriasViewModel,
                busquedaViewModel = busquedaViewModel
            )
        }

        composable(
            "NegocioDetallePage/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            NegocioDetallePage(
                id = backStackEntry.arguments?.getString("id"),
                navController = navController
            )
        }


        // Ruta simple para resultados sin categoría
        composable(Pantalla.RUTA_RESULTADOS_APP) {
            ResultadosPage(
                navController = navController,
                categoriasViewModel = categoriasViewModel,
                busquedaViewModel = busquedaViewModel
            )
        }

        // Grafo de navegación Nav bar - NEGOCIO
        composable(Pantalla.RUTA_INICIO_NEGOCIO) {
            InicioNegocioPage(navController)
        }
        composable(Pantalla.RUTA_PROMOCIONES_NEGOCIO) {
            Promociones(navController)
        }


        composable(
            route = "MapaPage?lat={lat}&lng={lng}",
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.FloatType
                    defaultValue = 0f // valor por defecto si no viene
                },
                navArgument("lng") {
                    type = NavType.FloatType
                    defaultValue = 0f
                }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat") ?: 0f
            val lng = backStackEntry.arguments?.getFloat("lng") ?: 0f

            // Si ambos son 0, asumimos que no se pasó ubicación del negocio
            MapaPage(
                navController = navController,
                lat = if (lat != 0f) lat.toDouble() else null,
                lng = if (lng != 0f) lng.toDouble() else null
            )
        }

        composable(Pantalla.RUTA_PERFIL_NEGOCIO) {
            PerfilNegocioPage(navController)
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

/**
 * Barra de navegación inferior para usuarios jóvenes.
 *
 * Muestra las principales secciones de la aplicación con iconos animados
 * y efectos de selección.
 *
 * @param navController Controlador de navegación para manejar los cambios de pantalla
 */
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