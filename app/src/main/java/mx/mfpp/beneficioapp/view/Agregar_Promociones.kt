package mx.mfpp.beneficioapp.view

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Pantalla principal para agregar nuevas promociones dentro del sistema.
 *
 * Permite al usuario seleccionar una imagen, ingresar título, descripción,
 * descuento, categoría, y rango de fechas de validez. Los campos son validados
 * visualmente, y la información se guarda al presionar el botón principal.
 *
 * @param navController Controlador de navegación de la aplicación.
 * @param modifier Modificador opcional para ajustar la apariencia del contenedor principal.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Agregregar_Promociones(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var descuento by rememberSaveable { mutableStateOf("") }
    var desde by rememberSaveable { mutableStateOf("") }
    var hasta by rememberSaveable { mutableStateOf("") }
    var expiraEn by rememberSaveable { mutableStateOf<Int?>(null) }

    val opciones = listOf("Belleza", "Comida", "Educación", "Salud", "Entretenimiento", "Moda", "Servicios")
    var categoria by rememberSaveable { mutableStateOf("") }


    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
        uri = result
    }

    Scaffold(
        topBar = { ArrowTopBar(navController, "Agregar Promoción") },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(top = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (uri == null) {
                    IconButton(
                        onClick = { pickImage.launch("image/*") },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF7C3AED)
                        ),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Seleccionar imagen"
                        )
                    }
                } else {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Imagen de la promoción",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    // Botón para cambiar imagen
                    IconButton(
                        onClick = { pickImage.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Cambiar imagen",
                            tint = Color.White
                        )
                    }
                }
            }
            Etiqueta("Título", true)
            BeneficioOutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it.take(60) },
                placeholder = "Escribe aquí"
            )

            Etiqueta("Descripción", true)
            BeneficioOutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it.take(150) },
                placeholder = "Escribe aquí"
            )

            Etiqueta("Descuento", false)
            BeneficioOutlinedTextField(
                value = descuento,
                onValueChange = { descuento = it.take(20) },
                placeholder = "Ej. 10% o 2x1"
            )

            SeleccionarCategoria(
                categoria = categoria,
                onCategoriaChange = { categoria = it },
                categorias = opciones
            )
            RangoFechasPicker(
                desde = desde,
                hasta = hasta,
                onDesdeChange = { desde = it },
                onHastaChange = { fecha, dias ->
                    hasta = fecha
                    expiraEn = dias
                }
            )

            // Botón Guardar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BotonMorado(navController, "Agregar", Pantalla.RUTA_INICIO_NEGOCIO)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private val fmtDDMMYYYY: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
/**
 * Selector de fecha inicial para la promoción.
 *
 * Muestra un campo de texto no editable con un diálogo de calendario
 * para elegir la fecha "Disponible desde".
 *
 * @param value Fecha actual seleccionada en formato dd/MM/yyyy.
 * @param onChange Callback que actualiza la fecha seleccionada.
 * @param label Etiqueta mostrada sobre el campo.
 * @param modifier Modificador opcional para el estilo.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaDesdePicker(
    value: String,
    onChange: (String) -> Unit,
    label: String = "Disponible desde",
    modifier: Modifier = Modifier
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = null)

    Etiqueta(label)
    Box {
        BeneficioOutlinedTextField(
            value = value,
            onValueChange = {},
            placeholder = "dd/MM/yyyy",
            readOnly = true,
            modifier = modifier
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { showPicker = true }
        )
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onChange(selected.format(fmtDDMMYYYY))
                    }
                    showPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
/**
 * Selector de fecha final de la promoción con validación de rango.
 *
 * No permite seleccionar una fecha anterior a la fecha de inicio.
 * Calcula automáticamente los días restantes hasta la expiración.
 *
 * @param value Fecha seleccionada en formato dd/MM/yyyy.
 * @param onChange Callback que devuelve la fecha seleccionada y días restantes.
 * @param label Etiqueta del campo (por defecto "Hasta").
 * @param minDesde Fecha mínima seleccionable.
 * @param modifier Modificador visual opcional.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaHastaPicker(
    value: String,
    onChange: (fechaStr: String, expiraEnDias: Int?) -> Unit,
    label: String = "Hasta",
    minDesde: String? = null,        // restringe selección
    modifier: Modifier = Modifier
) {
    val fmt = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    fun parseUtcOrNull(s: String?): LocalDate? = try {
        if (s.isNullOrBlank()) null else LocalDate.parse(s, fmt)
    } catch (_: Exception) { null }

    val minDate = remember(minDesde) { parseUtcOrNull(minDesde) }

    val selectableDates = remember(minDate) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val d = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                return minDate?.let { !d.isBefore(it) } ?: true
            }
        }
    }

    var showPicker by rememberSaveable { mutableStateOf(false) }
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = selectableDates
    )
    Etiqueta(label)
    Box {
        BeneficioOutlinedTextField(
            value = value,
            onValueChange = {},
            placeholder = "dd/MM/yyyy",
            readOnly = true,
            modifier = modifier
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { showPicker = true }
        )
    }
    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        val fechaStr = selected.format(fmt)
                        val dias = ChronoUnit.DAYS
                            .between(LocalDate.now(ZoneOffset.UTC), selected)
                            .toInt()
                            .coerceAtLeast(0)
                        onChange(fechaStr, dias)
                    }
                    showPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
/**
 * Selector compuesto de rango de fechas.
 *
 * Combina los campos "Desde" y "Hasta" e incluye validaciones visuales
 * para evitar rangos inconsistentes.
 *
 * @param desde Fecha inicial seleccionada.
 * @param hasta Fecha final seleccionada.
 * @param onDesdeChange Callback para actualizar la fecha inicial.
 * @param onHastaChange Callback para actualizar la fecha final y los días restantes.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RangoFechasPicker(
    desde: String,
    hasta: String,
    onDesdeChange: (String) -> Unit,
    onHastaChange: (String, Int?) -> Unit,
) {
    val fmt = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    fun parseUtcOrNull(s: String?): LocalDate? =
        try { if (s.isNullOrBlank()) null else LocalDate.parse(s, fmt) } catch (_: Exception) { null }

    var errorHastaMsg by rememberSaveable { mutableStateOf<String?>(null) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f)) {
                FechaDesdePicker(
                    value = desde,
                    onChange = { nuevaDesde ->
                        onDesdeChange(nuevaDesde)
                        val dDesde = parseUtcOrNull(nuevaDesde)
                        val dHasta = parseUtcOrNull(hasta)
                        if (dDesde != null && dHasta != null && dHasta.isBefore(dDesde)) {
                            onHastaChange("", null)
                            errorHastaMsg = "La fecha 'Hasta' no puede ser anterior a 'Disponible desde'. Selecciónala nuevamente."
                        } else errorHastaMsg = null
                    }
                )
            }
            Column(Modifier.weight(1f)) {
                FechaHastaPicker(
                    value = hasta,
                    minDesde = desde,
                    onChange = { fecha, dias ->
                        val dDesde = parseUtcOrNull(desde)
                        val dHasta = parseUtcOrNull(fecha)
                        if (dDesde != null && dHasta != null && dHasta.isBefore(dDesde)) {
                            errorHastaMsg = "La fecha 'Hasta' no puede ser anterior a 'Disponible desde'."
                            return@FechaHastaPicker
                        }
                        errorHastaMsg = null
                        onHastaChange(fecha, dias)
                    }
                )
            }
        }
        if (!errorHastaMsg.isNullOrEmpty()) {
            Text(
                text = errorHastaMsg!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp)
                    .align (Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        }

    }
}
/**
 * Componente desplegable para seleccionar la categoría de la promoción.
 *
 * Muestra una lista de categorías predefinidas y actualiza el valor seleccionado.
 *
 * @param categoria Categoría actual seleccionada.
 * @param onCategoriaChange Callback para actualizar la categoría.
 * @param categorias Lista de categorías disponibles.
 * @param obligatorio Indica si el campo es obligatorio.
 * @param modifier Modificador opcional.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarCategoria(
    categoria: String,
    onCategoriaChange: (String) -> Unit,
    categorias: List<String>,
    obligatorio: Boolean = true,
    modifier: Modifier = Modifier
) {
    Etiqueta("Categoría", obligatorio = obligatorio)

    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = categoria,
            onValueChange = { },                // solo lectura
            readOnly = true,
            placeholder = { BeneficioPlaceholder("Selecciona categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 13.dp, bottom = 8.dp)
                .height(53.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categorias.forEach { c ->
                DropdownMenuItem(
                    text = { Text(c) },
                    onClick = {
                        onCategoriaChange(c)
                        expanded = false
                    }
                )
            }
        }
    }
}
/**
 * Vista previa de la pantalla Agregar Promociones en modo de diseño.
 *
 * Permite visualizar la interfaz sin necesidad de ejecutar la app completa.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Agregar_PromocionesPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        Agregregar_Promociones(navController)
    }
}
