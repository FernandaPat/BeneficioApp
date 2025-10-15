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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.clip
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

/**
 * Botón morado con estilo primario para acciones principales.
 *
 * Utiliza colores de la marca con fondo suave y texto en morado fuerte.
 *
 * @param navController Controlador de navegación para manejar la navegación
 * @param texto Texto a mostrar en el botón
 * @param route Ruta de destino cuando se presiona el botón
 */
@Composable
fun BotonMorado(
    navController: NavController? = null,
    texto: String,
    route: String? = null,
    habilitado: Boolean = true,
    onClick: (() -> Unit)? = null // 🔹 Nuevo: callback opcional
) {
    val moradoFuerte = Color(0xFF9605f7)
    val moradoSuave = Color(0xFFE9d4ff)

    Button(
        onClick = {
            if (onClick != null) {
                onClick() // 🔹 Si te pasan un callback, lo ejecuta
            } else if (route != null && navController != null) {
                navController.navigate(route)
            }
        },
        enabled = habilitado,
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
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}


/**
 * Botón blanco con borde morado para acciones secundarias.
 *
 * Proporciona una alternativa visual menos prominente al botón morado.
 *
 * @param navController Controlador de navegación para manejar la navegación
 * @param texto Texto a mostrar en el botón
 * @param route Ruta de destino cuando se presiona el botón
 */
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

/**
 * Componente de texto para títulos principales centrados.
 *
 * Estilo consistente para títulos de pantalla con tipografía grande y negrita.
 *
 * @param texto Texto del título a mostrar
 */
@Composable
fun TextoTitulo(texto: String) {
    Text(
        text = texto,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    )
}

/**
 * Componente que muestra la imagen de la tarjeta digital del usuario.
 *
 * Representa visualmente la tarjeta de beneficio joven del usuario.
 */
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

/**
 * Componente de texto para subtítulos con estilo negrita y justificado.
 *
 * Utilizado para descripciones y textos informativos de tamaño medio.
 *
 * @param texto Texto a mostrar
 */
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

/**
 * Barra superior personalizada con flecha de retroceso y título.
 *
 * Proporciona navegación consistente con animaciones en el icono de retroceso.
 *
 * @param navController Controlador de navegación para manejar el retroceso
 * @param text Título a mostrar en la barra
 * @param modifier Modificador de Composable para personalizar el layout
 * @param barHeight Altura visual de la barra
 * @param topOffset Desplazamiento vertical del título
 */
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

/**
 * Barra superior personalizada para pantallas de negocio con opción de agregar.
 *
 * Extiende la barra básica con un botón de agregar en el lado derecho.
 *
 * @param navController Controlador de navegación para manejar el retroceso
 * @param text Título a mostrar en la barra
 * @param modifier Modificador de Composable para personalizar el layout
 * @param barHeight Altura visual de la barra
 * @param topOffset Desplazamiento vertical del título
 * @param showAdd Indica si mostrar el botón de agregar
 * @param onAddClick Callback invocado cuando se presiona el botón de agregar
 */
@Composable
fun ArrowTopBarNegocio(
    navController: NavController,
    text: String,
    modifier: Modifier = Modifier,
    barHeight: Dp = 72.dp,
    topOffset: Dp = 6.dp,
    showAdd: Boolean = false,             // <- NUEVO: mostrar u ocultar el botón +
    onAddClick: () -> Unit = {}           // <- NUEVO: callback del botón +
) {
    val interactionLeft = remember { MutableInteractionSource() }
    val (modLeft, _) = crearAnimacionIconosBotones(
        interactionSource = interactionLeft,
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
        // Flecha atrás (izquierda)
        IconButton(
            onClick = { navController.popBackStack() },
            interactionSource = interactionLeft,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Regresar",
                modifier = Modifier.size(30.dp).then(modLeft),
                tint = Color.Black
            )
        }

        // Título
        TextoMedioGrande(
            texto = text,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = topOffset)
        )

        // Botón "+" en círculo (derecha)
        if (showAdd) {
            val interactionRight = remember { MutableInteractionSource() }
            val (modRight, _) = crearAnimacionIconosBotones(
                interactionSource = interactionRight,
                colorNormal = Color.White,
                escalaActivado = 1.1f
            )
            IconButton(
                onClick = onAddClick,
                interactionSource = interactionRight,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .then(modRight)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(color = 0xFF9605f7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Componente de texto para títulos grandes centrados.
 *
 * Variante del texto medio grande con estilo específico para títulos.
 *
 * @param texto Texto a mostrar
 * @param modifier Modificador de Composable para personalizar el layout
 */
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

/**
 * Crea animaciones para iconos de botones con efectos de escala y color.
 *
 * Proporciona feedback visual cuando los botones son presionados.
 *
 * @param interactionSource Fuente de interacción para detectar estados de presión
 * @param estaSeleccionado Indica si el botón está en estado seleccionado
 * @param colorNormal Color normal del icono
 * @param colorActivado Color del icono cuando está activado/presionado
 * @param escalaActivado Factor de escala cuando está activado
 * @param duracion Duración de la animación en milisegundos
 * @return Par de modificador animado y color calculado
 */
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

/**
 * Crea animaciones para elementos de navegación con efectos de escala y color.
 *
 * Utilizado específicamente para items de la barra de navegación inferior.
 *
 * @param estaSeleccionado Indica si el elemento está seleccionado
 * @param colorNormal Color normal del elemento
 * @param colorActivado Color del elemento cuando está seleccionado
 * @param escalaActivado Factor de escala cuando está seleccionado
 * @param duracion Duración de la animación en milisegundos
 * @return Par de modificador animado y color calculado
 */
@Composable
fun crearAnimacionNavegacion(
    estaSeleccionado: Boolean,
    colorNormal: Color = Color.Gray,
    colorActivado: Color = Color(0xFF9605F7),
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