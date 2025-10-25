package mx.mfpp.beneficioapp.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.viewmodel.AgregarPromocionViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Pantalla para agregar una nueva promoción desde la vista del negocio.
 *
 * Permite al usuario seleccionar una imagen, escribir título, descripción y descuento,
 * además de elegir las fechas de vigencia mediante selectores de fecha.
 * Al finalizar, se guarda la promoción y se muestra un Snackbar en caso de error.
 *
 * @param navController Controlador de navegación para moverse entre pantallas.
 * @param viewModel ViewModel que gestiona el estado y la lógica de agregar una promoción.
 * @param modifier Modificador opcional para personalizar el contenedor.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgregarPromocion(
    navController: NavController,
    viewModel: AgregarPromocionViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.uri.value = uri
    }

    Scaffold(
        topBar = { ArrowTopBar(navController, "Agregar Promoción") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(top = 8.dp)
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
                if (viewModel.uri.value == null) {
                    IconButton(
                        onClick = { pickImage.launch("image/*") },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF7C3AED)
                        ),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Seleccionar imagen")
                    }
                } else {
                    AsyncImage(
                        model = viewModel.uri.value,
                        contentDescription = "Imagen de la promoción",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    IconButton(
                        onClick = { pickImage.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Cambiar imagen", tint = Color.White)
                    }
                }
            }

            Etiqueta("Título", true)
            BeneficioOutlinedTextField(
                value = viewModel.nombre.value,
                onValueChange = { viewModel.nombre.value = it.take(60) },
                placeholder = "Escribe aquí"
            )

            Etiqueta("Descripción", true)
            BeneficioOutlinedTextField(
                value = viewModel.descripcion.value,
                onValueChange = { viewModel.descripcion.value = it.take(150) },
                placeholder = "Escribe aquí"
            )

            Etiqueta("Descuento", true)
            BeneficioOutlinedTextField(
                value = viewModel.descuento.value,
                onValueChange = { viewModel.descuento.value = it.take(20) },
                placeholder = "Ej. 10% o 2x1"
            )

            RangoFechasPicker(
                desde = viewModel.desde.value,
                hasta = viewModel.hasta.value,
                onDesdeChange = { nuevaFechaDesde ->
                    viewModel.desde.value = nuevaFechaDesde
                    Log.d("FECHAS", "Desde seleccionada: $nuevaFechaDesde")
                },
                onHastaChange = { nuevaFechaHasta, _ ->
                    viewModel.hasta.value = nuevaFechaHasta
                    Log.d("FECHAS", "Hasta seleccionada: $nuevaFechaHasta")
                }
            )

            Spacer(Modifier.height(40.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonAction(
                    textoNormal = "Agregar",
                    textoCargando = "Agregando...",
                    isLoading = viewModel.isLoading.value,
                    habilitado = viewModel.uri.value != null,
                    onClick = {
                        if (viewModel.uri.value == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Selecciona una imagen antes de continuar.")
                            }
                            return@ButtonAction
                        }

                        viewModel.isLoading.value = true

                        viewModel.guardarPromocion(
                            context = context,
                            onSuccess = {
                                navController.navigate(Pantalla.RUTA_INICIO_NEGOCIO) {
                                    popUpTo(Pantalla.RUTA_AGREGAR_PROMOCIONES) { inclusive = true }
                                }
                                viewModel.isLoading.value = false
                            },
                            onError = { msg ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                                viewModel.isLoading.value = false
                            }
                        )
                    }
                )
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

/** Formato de fecha estándar dd/MM/yyyy. */
@RequiresApi(Build.VERSION_CODES.O)
val fmtDDMMYYYY: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/**
 * Selector de fecha "Disponible desde".
 *
 * Muestra un campo de texto de solo lectura que abre un selector de fecha al tocarlo.
 *
 * @param value Valor actual de la fecha.
 * @param onChange Callback que devuelve la fecha seleccionada.
 * @param label Etiqueta del campo.
 * @param modifier Modificador opcional.
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

    Etiqueta(label, true, modifier = Modifier.padding(start = 4.dp))
    Box {
        BeneficioOutlinedTextField(
            value = value,
            onValueChange = {},
            placeholder = "dd/MM/yyyy",
            readOnly = true,
            modifier = modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
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
 * Selector de fecha "Hasta".
 *
 * Permite elegir una fecha límite de vigencia, con validación para evitar fechas anteriores
 * a la fecha "Desde".
 *
 * @param value Valor actual del campo.
 * @param onChange Callback con la fecha seleccionada y los días restantes de vigencia.
 * @param label Etiqueta del campo.
 * @param minDesde Fecha mínima seleccionable.
 * @param modifier Modificador opcional.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaHastaPicker(
    value: String,
    onChange: (fechaStr: String, expiraEnDias: Int?) -> Unit,
    label: String = "Hasta",
    minDesde: String? = null,
    modifier: Modifier = Modifier,
) {
    val fmt = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    fun parseUtcOrNull(s: String?): LocalDate? =
        try { if (s.isNullOrBlank()) null else LocalDate.parse(s, fmt) } catch (_: Exception) { null }

    val minDate = remember(minDesde) { parseUtcOrNull(minDesde) }

    val selectableDates = remember(minDate) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val d = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                return minDate?.let { !d.isBefore(it) } ?: true
            }
        }
    }

    var showPicker by rememberSaveable { mutableStateOf(false) }
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = selectableDates
    )

    Etiqueta(label, true, modifier = Modifier.padding(start = 4.dp))
    Box {
        BeneficioOutlinedTextField(
            value = value,
            onValueChange = {},
            placeholder = "dd/MM/yyyy",
            readOnly = true,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
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
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

/**
 * Componente que combina los selectores de fecha "Desde" y "Hasta",
 * asegurando que la fecha final no sea anterior a la inicial.
 *
 * @param desde Fecha de inicio.
 * @param hasta Fecha de finalización.
 * @param onDesdeChange Callback para actualizar la fecha de inicio.
 * @param onHastaChange Callback para actualizar la fecha final y duración.
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

    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        FechaDesdePicker(
            value = desde,
            onChange = { nuevaDesde ->
                onDesdeChange(nuevaDesde)
                val dDesde = parseUtcOrNull(nuevaDesde)
                val dHasta = parseUtcOrNull(hasta)
                if (dDesde != null && dHasta != null && dHasta.isBefore(dDesde)) {
                    onHastaChange("", null)
                    errorHastaMsg = "La fecha 'Hasta' no puede ser anterior a 'Disponible desde'."
                } else errorHastaMsg = null
            }
        )
    }
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
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

    if (!errorHastaMsg.isNullOrEmpty()) {
        Text(
            text = errorHastaMsg!!,
            color = MaterialTheme.colorScheme.error,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Vista previa de la pantalla [AgregarPromocion] para el modo diseño.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Agregar_PromocionesPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        AgregarPromocion(navController)
    }
}