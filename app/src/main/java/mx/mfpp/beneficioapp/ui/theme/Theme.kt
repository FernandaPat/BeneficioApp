package mx.mfpp.beneficioapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AlwaysLightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun BeneficioAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AlwaysLightColorScheme,
        typography = Typography,
        content = content
    )
}
