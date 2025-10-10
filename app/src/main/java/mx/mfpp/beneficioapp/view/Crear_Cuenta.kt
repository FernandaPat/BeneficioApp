package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.Calendar
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.OutlinedTextFieldDefaults
import mx.mfpp.beneficioapp.model.Direccion
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.text.style.TextDecoration


@Composable
fun Crear_Cuenta(navController: NavController, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    var dia by rememberSaveable { mutableStateOf<Int?>(null) }
    var direccion by remember { mutableStateOf(Direccion()) }
    var password by rememberSaveable { mutableStateOf("") }
    var tieneTarjeta by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var folio by rememberSaveable { mutableStateOf("") }
    Scaffold(
        topBar = { ArrowTopBar(navController, "Crear Cuenta") },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .imePadding()
                .padding(paddingValues)
                .padding(top = 3.dp)
        ) {
            Etiqueta("Nombre(s)",true)
            CapturaTexto("Escribe aquí", 30)
            Etiqueta("Apellido Materno", true)
            CapturaTexto("Escribe aquí", 30)
            Etiqueta("Apellido Paterno" , true)
            CapturaTexto("Escribe aquí", 30)
            Etiqueta("CURP",true)
            CapturaTexto("CURP", 18)
            Etiqueta(texto = "Fecha de Nacimiento", true)
            Fecha_nacimiento(
                onFechaChange = { d, m, a -> }
            )
            SeccionDireccion { dir -> direccion = dir }

            Etiqueta("Género",true)
            SeleccionarGenero(listOf("Femenino", "Masculino", "Otro"))
            Etiqueta("Correo", true)
            CapturaTexto("correo@ejemplo.com", 50)
            Etiqueta("Número de teléfono", true)
            CapturaNumeroTelefono("10 dígitos")
            Etiqueta("Contraseña", true)
            BeneficioPasswordField(
                value = password,
                onValueChange = { input -> password = input.take(16)},
                placeholder = "Mín. 8 caracteres"
            )
            PasswordChecklist(password)

            Etiqueta("¿Cuentas con una tarjeta física?", obligatorio = true)
            Tarjeta(
                seleccion = tieneTarjeta,
                onSelected = { si ->
                    tieneTarjeta = si
                    if (!si) folio = ""
                }
            )

            if (tieneTarjeta == true) {
                Etiqueta("Ingresa el número de folio", obligatorio = true)
                BeneficioOutlinedTextField(
                    value = folio,
                    onValueChange = { input -> folio = input.filter { it.isDigit() }.take(16) },
                    placeholder = "1234 5678 9012 3456",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )

                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BotonMorado(navController, "Crear Cuenta", Pantalla.RUTA_INICIAR_SESION)

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Ya tienes una cuenta? ", fontSize = 11.sp, color = Color.Black)

                    Text(
                        text = "Inicia sesión aquí",
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(Pantalla.RUTA_INICIAR_SESION)
                        }
                    )
                }
            }
        }
    }
}


fun Modifier.beneficioInput(): Modifier = this
    .padding(start = 20.dp, end = 20.dp, bottom = 8.dp, top = 13.dp)
    .width(380.dp)
    .height(53.dp)

@Composable
fun SeccionDireccion(
    modifier: Modifier = Modifier,
    onAddressChange: (Direccion) -> Unit = {}
) {
    var calle by rememberSaveable { mutableStateOf("") }
    var numExt by rememberSaveable { mutableStateOf("") }
    var numInt by rememberSaveable { mutableStateOf("") }
    var colonia by rememberSaveable { mutableStateOf("") }
    var cp by rememberSaveable { mutableStateOf("") }
    var municipio by rememberSaveable { mutableStateOf("") }
    var estado by rememberSaveable { mutableStateOf("") }

    fun notify() = onAddressChange(
        Direccion(
            calle = calle,
            numeroExterior = numExt,
            numeroInterior = numInt,
            colonia = colonia,
            codigoPostal = cp,
            municipio = municipio,
            estado = estado
        )
    )

    // Calle
    Etiqueta("Calle",true)
    BeneficioOutlinedTextField(
        value = calle,
        onValueChange = { input -> calle = input.take(60); notify() },
        placeholder = "Lázaro Cárdenas",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )

    // Número exterior / interior
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Etiqueta("Núm. exterior",true)
            OutlinedTextField(
                value = numExt,
                onValueChange = { input ->
                    numExt = input.filter { it.isLetterOrDigit() || it == '-' }.take(8); notify()
                },
                placeholder = { BeneficioPlaceholder("123") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp)
                    .clip(RoundedCornerShape(10.dp)),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }
        Column(Modifier.weight(1f)) {
            Etiqueta("Núm. interior",false)
            OutlinedTextField(
                value = numInt,
                onValueChange = { input ->
                    numInt = input.filter { it.isLetterOrDigit() || it == '-' }.take(8); notify()
                },
                placeholder = { BeneficioPlaceholder("12B") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp)
                    .clip(RoundedCornerShape(10.dp)),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }
    }

    // Colonia
    Etiqueta("Colonia",true)
    BeneficioOutlinedTextField(
        value = colonia,
        onValueChange = { input -> colonia = input.take(60); notify() },
        placeholder = "Las Arboledas",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )

    // Código Postal (5 dígitos)
    Etiqueta("Código Postal",true)
    BeneficioOutlinedTextField(
        value = cp,
        onValueChange = { input -> cp = input.filter { it.isDigit() }.take(5); notify() },
        placeholder = "52900",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
    )

    // Municipio
    Etiqueta("Municipio / Alcaldía", true)
    BeneficioOutlinedTextField(
        value = municipio,
        onValueChange = { input -> municipio = input.take(40); notify() },
        placeholder = "Ciudad López Mateos",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    Etiqueta(texto = "Estado", true)
    SeleccionarEstado(
        onSelected = { sel ->
            estado = sel
            notify()
        }
    )
}
@Composable
fun Tarjeta(
    seleccion: Boolean?,
    onSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, top = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OpcionPill(text = "Sí", selected = seleccion == true) { onSelected(true) }
        Spacer(Modifier.width(16.dp))
        OpcionPill(text = "No", selected = seleccion == false) { onSelected(false) }
    }
}

@Composable
private fun OpcionPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val bg    = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color(0xFFF5F5F5)
    val bd    = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)

    Row(
        modifier = Modifier
            .clip(shape)
            .border(1.dp, bd, shape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(1.dp, bd, RoundedCornerShape(5.dp))
                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Text(
            text = "  $text",
            color = Color.Black,
            fontSize = 14.sp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarEstado(
    modifier: Modifier = Modifier,
    onSelected: (String) -> Unit
) {
    val opciones = listOf("Estado de México", "Ciudad de México", "Otro")
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selected.orEmpty(),
                onValueChange = {},
                readOnly = true,
                placeholder = { BeneficioPlaceholder("Selecciona estado") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .beneficioInput()
                    .height(53.dp)
                    .menuAnchor(),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            selected = opcion
                            onSelected(opcion)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (selected == "Otro") {
            Text(
                text = "Gracias por tu interés; sin embargo, ten en cuenta que los negocios aquí presentes son solo del municipio de Atizapán.",
                color = Color(0xFFB00020),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp)
            )
        }
    }
}


@Composable
fun Fecha_nacimiento(
    modifier: Modifier = Modifier,
    yearRange: IntRange = (1900..Calendar.getInstance().get(Calendar.YEAR)),
    onFechaChange: (Int?, Int?, Int?) -> Unit = { _, _, _ -> }
) {
    FechaNacimientoDropdowns(
        modifier = modifier,
        yearRange = yearRange,
        onDateSelected = onFechaChange
    )
    modifier.beneficioInput()

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FechaNacimientoDropdowns(
    modifier: Modifier = Modifier,
    yearRange: IntRange,
    onDateSelected: (d: Int?, m: Int?, y: Int?) -> Unit
) {
    var dia by rememberSaveable { mutableStateOf<Int?>(null) }
    var mes by rememberSaveable { mutableStateOf<Int?>(null) }
    var anio by rememberSaveable { mutableStateOf<Int?>(null) }

    fun daysInMonth(m: Int?, y: Int?): Int = when (m) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
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
            modifier = Modifier
                .weight(1f)
                .padding(top = 20.dp)
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
            modifier = Modifier
                .weight(1f)
                .padding(top = 20.dp)
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
            modifier = Modifier
                .weight(1f)
                .padding(top = 20.dp)
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
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { BeneficioPlaceholder(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(53.dp)
                .clip(RoundedCornerShape(3.dp)),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
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
fun BeneficioPlaceholder(text: String) {
    Text(
        text = text,
        color = Color(0xFFA1A0A0),
        style = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp),

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

        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(

            cursorColor = Color.Black
        ),
        modifier = modifier.beneficioInput(),
        keyboardOptions = keyboardOptions
    )

}
@Composable
fun BeneficioPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { BeneficioPlaceholder(placeholder) },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
            val desc   = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = desc)
            }
        },
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.Black
        ),
        modifier = modifier
            .beneficioInput()
            .height(53.dp)
    )
}
@Composable
fun PasswordChecklist(
    password: String,
    modifier: Modifier = Modifier
) {
    val hasMinLen  = remember(password) { password.length >= 8 }
    val hasUpper    = remember(password) { password.any { it.isUpperCase() } }
    val hasDigit    = remember(password) { password.any { it.isDigit() } }
    val hasSpecial  = remember(password) {
        val specials = Regex("""[!@#${'$'}%\^&*()_+\-=\[\]{}\\|:";'<>?,./¿¡]""")
        specials.containsMatchIn(password)
    }
    val okColor   = Color(0xFF2E7D32)
    val idleColor = Color(0xFF9E9E9E)

    Column(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp)
            .padding(top = 4.dp)
    ) {
        ChecklistRow(texto = "Al menos 8 caracteres", activo = hasMinLen, okColor = okColor, idleColor = idleColor)
        ChecklistRow(texto = "Tiene una mayúscula", activo = hasUpper, okColor = okColor, idleColor = idleColor)
        ChecklistRow(texto = "Tiene un número",     activo = hasDigit, okColor = okColor, idleColor = idleColor)
        ChecklistRow(texto = "Tiene un carácter especial (¿ ? ! @ # ...)", activo = hasSpecial, okColor = okColor, idleColor = idleColor)
    }
}

@Composable
private fun ChecklistRow(
    texto: String,
    activo: Boolean,
    okColor: Color,
    idleColor: Color
) {
    val tint = if (activo) okColor else idleColor
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        Icon(
            imageVector = if (activo) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff,
            contentDescription = null,
            tint = tint
        )
        Text(
            text = "  $texto",
            color = tint,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
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
    modifier: Modifier = Modifier,

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
fun Etiqueta(
    texto: String,
    obligatorio: Boolean = false,
    modifier: Modifier = Modifier
) {
    val rojo = MaterialTheme.colorScheme.error
    Text(
        text = buildAnnotatedString {
            append(texto)
            if (obligatorio) {
                append(" ")
                withStyle(SpanStyle(color = rojo)) { append("*") }
            }
        },
        style = MaterialTheme.typography.labelLarge.copy(
            color = Color.Black,
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
    onSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier

) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.orEmpty(),
            onValueChange = {},
            readOnly = true,
            placeholder = { BeneficioPlaceholder("Seleccione una opción") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .beneficioInput()
                .height(53.dp)
                .menuAnchor(),
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
        val navController = rememberNavController()
        Crear_Cuenta(navController)
    }
}