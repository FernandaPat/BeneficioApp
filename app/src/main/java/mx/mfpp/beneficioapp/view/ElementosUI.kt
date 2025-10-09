package mx.mfpp.beneficioapp.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun BotonMorado(
    navController: NavController,
    texto: String,
    route: String
) {
    val moradoFuerte = Color(0xFF9605f7)
    val moradoSuave = Color(0xFFE9d4ff)

    Button(
        onClick = { navController.navigate(route) },
        colors = ButtonDefaults.buttonColors(containerColor = moradoSuave),
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier
            .width(250.dp)
            .height(60.dp)
    ) {
        Text(
            text = texto,
            color = moradoFuerte,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun BotonBlanco(
    navController: NavController,
    texto: String,
    route: String
) {
    val moradoFuerte = Color(0xFF9605f7)
    val moradoSuave = Color(0xFFE9d4ff)

    OutlinedButton(
        onClick = { navController.navigate(route) },
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        border = BorderStroke(2.dp, moradoSuave),
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier
            .width(250.dp)
            .height(60.dp)
    ) {
        Text(
            text = texto,
            color = moradoFuerte,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
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
            color = Color.Black,
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
            color = Color.Black,
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
        escalaActivado = 1.3f
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color.White)
            .height(barHeight)
            .padding(bottom = 5.dp)
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
                tint = Color.Black
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
        color = Color.Black
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

