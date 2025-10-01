package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
        .padding(top = 30.dp)) {
        Titulo("Crear Cuenta")
        Etiqueta("Nombre(s)")
        CapturaTexto("Escribe aquí",30)
        Etiqueta("Apellido Materno")
        CapturaTexto("Escribe aquí",30)
        Etiqueta("Apellido Paterno")
        CapturaTexto("Escribe aquí",30)
        Etiqueta(texto = "Fecha de Nacimiento")
        Fecha_nacimiento(
            onFechaChange = { d, m, a ->}
        )
        Etiqueta("Dirección")
        CapturaTexto("Escribe aquí",100)
        Etiqueta("Género")
        SeleccionarGenero(listOf("Femenino", "Masculino", "Otro"))
        Etiqueta("Correo")
        CapturaTexto("correo@ejemplo.com",50)
        Etiqueta("Celular")
        CapturaNumeroTelefono("10 dígitos")
        Etiqueta("Contraseña")
        CapturaTexto("8 caracteres",8)
    }
}
fun Modifier.beneficioInput(): Modifier = this
    .padding(start = 20.dp, top = 20.dp)
    .width(380.dp)
    .height(51.dp)
    .clip(RoundedCornerShape(10.dp))
@Composable
private fun beneficioTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor   = Color(0xFF170033),
    unfocusedContainerColor = Color(0xFF170033),
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedPlaceholderColor = Color(0xFFE6E1E5),
    unfocusedPlaceholderColor = Color(0xFFE6E1E5),
    cursorColor = Color.White
)

@Composable
private fun beneficioTextStyle() = LocalTextStyle.current.copy(
    fontSize = 16.sp,
    lineHeight = 20.sp,
    color = Color(0xFFF2ECF4)
)
@Composable
fun Fecha_nacimiento(
    modifier: Modifier = Modifier,
    yearRange: IntRange = (1900..Calendar.getInstance().get(Calendar.YEAR)),
    onFechaChange: (Int?, Int?, Int?) -> Unit = { _,_,_ -> }
) {
    // Si ya pones la etiqueta fuera, puedes quitar esta línea:
    // Etiqueta(texto = "Fecha de Nacimiento")

    FechaNacimientoDropdowns(
        modifier = modifier,
        yearRange = yearRange,
        onDateSelected = onFechaChange
    )
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
    yearRange: IntRange,
    onDateSelected: (d: Int?, m: Int?, y: Int?) -> Unit
) {
    var dia  by rememberSaveable { mutableStateOf<Int?>(null) }
    var mes  by rememberSaveable { mutableStateOf<Int?>(null) }
    var anio by rememberSaveable { mutableStateOf<Int?>(null) }

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
            placeholder = "Día",
            range = 1..maxDia,
            selected = dia,
            onSelected = { dia = it; onDateSelected(dia, mes, anio) },
            modifier = Modifier.weight(1f).padding(top = 20.dp)
        )
        NumberDropdownField(
            placeholder = "Mes",
            range = 1..12,
            selected = mes,
            onSelected = {
                mes = it
                val nuevoMax = daysInMonth(mes, anio)
                if ((dia ?: 0) > nuevoMax) dia = nuevoMax
                onDateSelected(dia, mes, anio)
            },
            modifier = Modifier.weight(1f).padding(top = 20.dp)
        )
        NumberDropdownField(
            placeholder = "Año",
            range = yearRange,
            selected = anio,
            onSelected = {
                anio = it
                val nuevoMax = daysInMonth(mes, anio)
                if ((dia ?: 0) > nuevoMax) dia = nuevoMax
                onDateSelected(dia, mes, anio)
            },
            modifier = Modifier.weight(1f).padding(top = 20.dp)

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberDropdownField(
    placeholder: String,
    range: IntRange,
    selected: Int?,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier   // <- este recibe el weight(1f) del Row
    ) {
        OutlinedTextField(
            value = selected?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { BeneficioPlaceholder(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()           // <- CLAVE para que se vea
                .height(51.dp)
                .clip(RoundedCornerShape(10.dp)),
            textStyle = beneficioTextStyle(),
            colors   = beneficioTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Si quieres años descendentes: for (n in range.reversed()) { ... }
            for (n in range) {
                DropdownMenuItem(
                    text = { Text(n.toString()) },
                    onClick = { onSelected(n); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun BeneficioPlaceholder(text: String) {
    Text(
        text = text,
        color = Color(0xFFE6E1E5),
        style = LocalTextStyle.current.copy(fontSize = 16.sp, lineHeight = 20.sp),
        lineHeight = 20.sp
    )
}
@Composable
fun BeneficioOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { BeneficioPlaceholder(placeholder) },
        singleLine = singleLine,
        readOnly = readOnly,
        modifier = modifier.beneficioInput(),
        textStyle = beneficioTextStyle(),
        colors = beneficioTextFieldColors(),
        keyboardOptions = keyboardOptions
    )
}
@Composable
fun CapturaNumeroTelefono(
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var texto by rememberSaveable { mutableStateOf("") }
    BeneficioOutlinedTextField(
        value = texto,
        onValueChange = { input -> texto = input.filter { it.isDigit() }.take(10) },
        placeholder = placeholder,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}
@Composable
fun CapturaTexto(
    placeholder: String,
    maxLength: Int? = null,
    modifier: Modifier = Modifier
) {
    var texto by rememberSaveable { mutableStateOf("") }
    BeneficioOutlinedTextField(
        value = texto,
        onValueChange = { input -> texto = maxLength?.let { input.take(it) } ?: input },
        placeholder = placeholder,
        modifier = modifier
    )
}
@Composable
fun Titulo(texto: String,modifier: Modifier = Modifier) {
    Text(
        text= texto,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
        modifier = Modifier
            .padding(top = 50.dp)
            .fillMaxWidth()
            .width(412.dp)
    )
}
@Composable
fun Etiqueta(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge.copy(
            color = Color.White,
            fontSize = 16.sp,
            lineHeight = 20.sp
        ),
        modifier = modifier.padding(start = 20.dp, top = 20.dp, end = 16.dp)
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarGenero(
    options: List<String> = listOf("Femenino", "Masculino", "Otro"),
    modifier: Modifier = Modifier,
    onSelected: (String) -> Unit = {}
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