package mx.mfpp.beneficioapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Define la configuración de tipografía personalizada para el [MaterialTheme] de la aplicación.
 *
 * Sobrescribe los estilos de texto predeterminados de Material Design 3
 * (como `bodyLarge`, `titleLarge`, etc.) con las definiciones específicas
 * de la app.
 */
val Typography = Typography(
    /**
     * Estilo de texto principal para cuerpos de texto largos.
     * Se utiliza como el estilo predeterminado para el Composable `Text()`.
     */
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Otros estilos de texto predeterminados que se pueden sobrescribir
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)