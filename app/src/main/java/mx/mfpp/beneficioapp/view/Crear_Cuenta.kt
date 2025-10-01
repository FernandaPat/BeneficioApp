package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar


@Composable
fun Crear_Cuenta(modifier: Modifier = Modifier) {
    Column  (modifier = modifier
        .fillMaxSize()
        .background(Color(0xFF230448))
        .padding(16.dp)) {
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
        style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
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
            .fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        colors = OutlinedTextFieldDefaults.colors(
            focusedPlaceholderColor = Color.White,
        unfocusedPlaceholderColor = Color.White))
    val scrollState = rememberScrollState()
    var dia by rememberSaveable { mutableStateOf<Int?>(null) }
    Column  (modifier = modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .imePadding()
        .background(Color(0xFF230448))
        Titulo("Crear Cuenta")
        Etiqueta("Nombre(s)")
        CapturaTexto("Escribe aquí",30)
        Etiqueta("Apellido Materno")
        CapturaTexto("Escribe aquí",30)
        Etiqueta("Apellido Paterno")
        CapturaTexto("Escribe aquí",30)
        Etiqueta(texto = "Fecha de Nacimiento")
        Fecha_nacimiento(
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Seleccionar_Genero(
    options: List<String> = listOf("Femenino", "Masculino", "Otro"),
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
private fun FechaNacimientoDropdowns(
    modifier: Modifier = Modifier,
    onDateSelected: (d: Int?, m: Int?, y: Int?) -> Unit
) {
    var dia  by rememberSaveable { mutableStateOf<Int?>(null) }
    var mes  by rememberSaveable { mutableStateOf<Int?>(null) }

    fun daysInMonth(m: Int?, y: Int?): Int = when (m) {
        1,3,5,7,8,10,12 -> 31
        4,6,9,11 -> 30
        2 -> if (y != null && ((y % 4 == 0 && y % 100 != 0) || (y % 400 == 0))) 29 else 28
        else -> 31
    }
    val maxDia = daysInMonth(mes, anio)

    Row(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NumberDropdownField(
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
            placeholder = {Text("Seleccione una opcion",color = Color.White)},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        )
    ) {
        OutlinedTextField(
            value = selected.orEmpty(),
            onValueChange = {},
            readOnly = true,
            placeholder = { BeneficioPlaceholder("Seleccione una opción") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .beneficioInput()
                .height(51.dp)
                .menuAnchor(),
            textStyle = beneficioTextStyle(),
            colors   = beneficioTextFieldColors()
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
                        onSelected(option)
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