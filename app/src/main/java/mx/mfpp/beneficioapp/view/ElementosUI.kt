package mx.mfpp.beneficioapp.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun TextoMedioGrande (texto: String) {
    Text(
        text = texto,
        modifier = Modifier
            .fillMaxWidth(),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
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