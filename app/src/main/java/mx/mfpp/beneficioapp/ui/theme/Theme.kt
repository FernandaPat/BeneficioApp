package mx.mfpp.beneficioapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Esquema de color personalizado que *fuerza* un tema claro (Light Theme).
 *
 * Define colores específicos (primario como Negro, fondo como Blanco)
 * ignorando la configuración del sistema (modo oscuro/claro).
 */
private val AlwaysLightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

/**
 * Contenedor de tema principal para la aplicación "BeneficioApp".
 *
 * Este composable aplica el [MaterialTheme] a toda la jerarquía de
 * composables descendiente.
 *
 * Utiliza [AlwaysLightColorScheme] para asegurar que la aplicación
 * siempre utilice un tema claro (fondo blanco, texto negro),
 * independientemente de la configuración del sistema Android.
 *
 * @param content El contenido Composable (UI) al que se le aplicará este tema.
 */
@Composable
fun BeneficioAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AlwaysLightColorScheme,
        typography = Typography, // Asume que 'Typography' está definido en otro archivo
        content = content
    )
}