package mx.mfpp.beneficioapp.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.mfpp.beneficioapp.R


@Composable
fun BotonRosa(navController: NavController, texto: String, route: String) {
    val coloresDegradado = listOf(
        Color(0xFFFF895E),
        Color(0xFFFF6F6B),
        Color(0xFFFF4580),
        Color(0xFFFF2291),
        Color(0xFFFF00A1)
    )

    val brushDegradado = Brush.horizontalGradient(colors = coloresDegradado)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(55.dp)
    ) {
        if (isPressed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brushDegradado, RoundedCornerShape(30.dp))
            )
        }

        if (!isPressed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, brushDegradado, RoundedCornerShape(30.dp))
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    navController.navigate(route)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun BotonBlanco(navController: NavController, texto: String, route: String) {
    val coloresDegradado = listOf(
        Color(0xFFFF895E),
        Color(0xFFFF6F6B),
        Color(0xFFFF4580),
        Color(0xFFFF2291),
        Color(0xFFFF00A1)
    )

    val brushDegradado = Brush.horizontalGradient(colors = coloresDegradado)
    val colorTexto = Color(0xFF230448) // Color morado para el texto

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(55.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isPressed) brushDegradado else Brush.linearGradient(
                        colors = listOf(Color.White, Color.White)
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    navController.navigate(route)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isPressed) Color.White else colorTexto // Texto blanco cuando está presionado, morado por defecto
                )
            )
        }
    }
}

@Composable
fun TextoTitulo(texto: String) {
    Text(
        text = texto,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 120.dp),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun CardImage() {
    Image(
        painter = painterResource(id = R.drawable.card),
        contentDescription = "Tarjeta digital Beneficio Joven",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(200.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun TextoMedioBold(texto: String) {
    Text(
        text = texto,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White,
            textAlign = TextAlign.Justify,
            lineHeight = 25.sp
        )
    )
}

@Composable
fun ArrowTopBar(
    navController: NavController,
    text: String,
    modifier: Modifier = Modifier,
    barHeight: Dp = 72.dp,   // ajusta altura visual
    topOffset: Dp = 6.dp      // baja un poco el título
) {
    val interactionSource = remember { MutableInteractionSource() }
    val (modifierAnimado, colorAnimado) = crearAnimacionIconosBotones(
        interactionSource = interactionSource,
        colorNormal = Color.White,
        colorActivado = Color(0xFFFF2291),
        escalaActivado = 1.3f
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF230448))
            .statusBarsPadding()        // evita superponerse con la barra de estado/notch
            .height(barHeight)
            .padding(bottom = 16.dp)    // tu padding inferior original
    ) {
        // Flecha atrás
        IconButton(
            onClick = { navController.popBackStack() },
            interactionSource = interactionSource,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Regresar",
                modifier = Modifier
                    .size(30.dp)
                    .then(modifierAnimado),
                tint = colorAnimado
            )
        }

        // Título centrado absoluto y un pelín más abajo
        TextoMedioGrande(
            texto = text,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = topOffset)
        )
    }
}

@Composable
fun TextoMedioGrande(
    texto: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = texto,
        modifier = modifier
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        ),
        color = Color.White
    )
}

// Aminacion botones
@Composable
fun crearAnimacionIconosBotones(
    interactionSource: MutableInteractionSource, // Ahora es obligatorio
    estaSeleccionado: Boolean = false,
    colorNormal: Color = Color.White,
    colorActivado: Color = Color(0xFFFF2291),
    escalaActivado: Float = 1.3f,
    duracion: Int = 200
): Pair<Modifier, Color> {

    val isPressed by interactionSource.collectIsPressedAsState()
    val estaActivado = if (!estaSeleccionado) isPressed else estaSeleccionado

    val scale by animateFloatAsState(
        targetValue = if (estaActivado) escalaActivado else 1f,
        animationSpec = tween(durationMillis = duracion)
    )

    val modifierAnimado = Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

    val colorAnimado = if (estaActivado) colorActivado else colorNormal

    return Pair(modifierAnimado, colorAnimado)
}



@Composable
fun crearAnimacionNavegacion(
    estaSeleccionado: Boolean,
    colorNormal: Color = Color.White,
    colorActivado: Color = Color(0xFFFF2291),
    escalaActivado: Float = 1.3f,
    duracion: Int = 200
): Pair<Modifier, Color> {

    val scale by animateFloatAsState(
        targetValue = if (estaSeleccionado) escalaActivado else 1f,
        animationSpec = tween(durationMillis = duracion)
    )

    val modifierAnimado = Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

    val colorAnimado = if (estaSeleccionado) colorActivado else colorNormal

    return Pair(modifierAnimado, colorAnimado)
}

