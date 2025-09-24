package mx.mfpp.beneficioapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.ui.theme.BeneficioAppTheme
import mx.mfpp.beneficioapp.viewmodel.BeneficioJovenVM

class MainActivity : ComponentActivity() {

    // ViewModel
    private val viewModel: BeneficioJovenVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeneficioAppTheme {
                AppPrincipal(viewModel)
            }
        }
    }
}

@Composable
fun AppPrincipal(beneficioJovenVM: BeneficioJovenVM, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { AppBottomBar(navController)},
    ) { innerPadding ->
        AppNavHost(beneficioJovenVM,
            navController = navController,
            modifier = modifier.padding(innerPadding))

    }
}

@Composable
fun AppNavHost(beneficioJovenVM: BeneficioJovenVM, navController: NavHostController, modifier: Modifier){
    NavHost(navController = navController,
        startDestination = Pantalla.RUTA_INICIO_APP,
        modifier = modifier.fillMaxSize()
    ) {
        // Grafo de navegación
        composable(Pantalla.RUTA_INICIO_APP) {
            InicioPage(navController)
        }
        composable(Pantalla.RUTA_MAPA_APP) {
            MapaPage(navController)
        }
        composable(Pantalla.RUTA_TARJETA_APP) {
            //Agregar
        }
        composable(Pantalla.RUTA_BUSCAR_APP) {
            //Agregar
        }
        composable(Pantalla.RUTA_ACTIVIDAD_APP) {
            //Agregar
        }
    }
}

@Composable
fun AppBottomBar(navController: NavHostController) {
    BottomAppBar (
        modifier = Modifier.size(width = 412.dp, height = 87.dp)
    ){
        val pilaNavegacion by navController.currentBackStackEntryAsState()
        val pantallaActual = pilaNavegacion?.destination

        Pantalla.listaPantallas.forEach { pantalla ->
            NavigationBarItem(
                selected = pantallaActual?.route == pantalla.ruta,
                onClick = {
                    navController.navigate(pantalla.ruta) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                            inclusive = true
                        }

                        restoreState = true
                        launchSingleTop = true
                    }
                },
                label = {
                    Text(
                        pantalla.etiqueta,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold) },
                icon = {
                    Icon(
                        pantalla.icono,
                        pantalla.etiqueta,
                        modifier = Modifier.size(30.dp)
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BeneficioAppTheme {
        AppPrincipal(BeneficioJovenVM())
    }
}