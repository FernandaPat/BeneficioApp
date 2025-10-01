package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Iniciar_sesion(modifier: Modifier = Modifier) {
    Column (modifier = modifier
        .fillMaxSize()
        .imePadding()
        .background(Color(0xFF230448))
        .padding(top= 30.dp)) {
        Titulo("Iniciar Sesión")
        Etiqueta("Correo o número de teléfono")
        CapturaTexto("Escribe aquí",30)
        Etiqueta("Contraseña")
        CapturaTexto("8 caracteres",8)
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Iniciar_sesionPreview() {
    MaterialTheme {
        Iniciar_sesion()
    }
}