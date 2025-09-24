package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun Crear_Cuenta(modifier: Modifier = Modifier) {
    Column  () {
        Titulo()
        Etiqueta("Nombre(s)")
        CapturaTexto("Escribe aquí")
        Etiqueta("Apellido Materno")
        CapturaTexto("Escribe aquí")
        Etiqueta("Apellido Paterno")
        CapturaTexto("Escribe aquí")
        Etiqueta("Fecha de Nacimiento")
        CapturaTexto("Escribe aquí")
        Etiqueta("Dirección")
        CapturaTexto("Escribe aquí")
        Etiqueta("Género")
        Seleccionar_Genero(listOf("Femenino", "Masculino", "Otro"))


    }
}

@Composable
fun Titulo(modifier: Modifier = Modifier) {
    Text("Crear Cuenta",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )

}
@Composable
fun Etiqueta(texto: String, modifier: Modifier = Modifier) {
    Text(texto,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp))
}

@Composable
fun CapturaTexto(
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var texto by remember { mutableStateOf("") }
    OutlinedTextField(
        value = texto,
        onValueChange = { texto = it },
        placeholder = { Text(placeholder) },
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 5.dp, top = 1.dp)
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Seleccionar_Genero(
    options: List<String> = listOf("Femenino", "Masculino", "Otro"),
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 5.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Género") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selected = option
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CrearCuentaPreview() {
    MaterialTheme {
        Crear_Cuenta()
    }
}