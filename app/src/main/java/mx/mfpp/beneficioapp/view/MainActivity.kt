package mx.mfpp.beneficioapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(87.dp),
        containerColor = Color(0xFF230448) // morado
    ) {
        val pilaNavegacion by navController.currentBackStackEntryAsState()
        val pantallaActual = pilaNavegacion?.destination

        Pantalla.listaPantallas.forEach { pantalla ->
            val isSelected = pantallaActual?.route == pantalla.ruta

            // Animación de escala para el icono
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.3f else 1f)

            // Gradiente para el label
            val gradientBrush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF2291), Color(0xFFFF7867))
            )

            NavigationBarItem(
                selected = isSelected,
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
                icon = {
                    Icon(
                        painter = painterResource(id = pantalla.iconoResId),
                        contentDescription = pantalla.etiqueta,
                        modifier = Modifier
                            .size(30.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale },
                        tint = if (isSelected) Color(0xFFFF2291) else Color.White
                    )
                },
                label = {
                    Text(
                        pantalla.etiqueta,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        style = if (isSelected) {
                            TextStyle(brush = gradientBrush)
                        } else {
                            TextStyle(color = Color.White)
                        }
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, // se ignora
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White, // se ignora
                    unselectedTextColor = Color.White,
                    indicatorColor = Color.Transparent
                )
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