package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mx.mfpp.beneficioapp.viewmodel.BeneficioJovenVM

@Composable
fun Iniciar_Sesion(navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Text(
                    text = "Pantalla Mapa Test",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(onClick = { navController.navigate("creditos") }) {
                    Text(text = "Ir a créditos")
                }
            }
        }
    }
}
