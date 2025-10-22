package mx.mfpp.beneficioapp.view

import android.os.Build
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.viewmodel.EditarPromocionViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Editar_Promociones(
    navController: NavController,
    idPromocion: Int,
    viewModel: EditarPromocionViewModel = viewModel()
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 📥 Cargar datos de la promoción al entrar
    LaunchedEffect(idPromocion) {
        viewModel.cargarPromocion(idPromocion) {}
    }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onNuevaImagen(it, context) }
    }

    Scaffold(
        topBar = { ArrowTopBar(navController, "Editar Promoción") },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scroll)
                .padding(padding)
        ) {
            // 📸 Imagen de promoción
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                val imagenActual = viewModel.uri.collectAsState().value ?: viewModel.imagenUrl.collectAsState().value

                if (imagenActual == null || (imagenActual is String && imagenActual.isBlank())) {
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
                        model = imagenActual,
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

            // 🧾 Campos de texto
            Etiqueta("Título", true)
            BeneficioOutlinedTextField(
                value = viewModel.nombre.collectAsState().value,
                onValueChange = { viewModel.nombre.value = it },
                placeholder = "Título de la promoción"
            )

            Etiqueta("Descripción", true)
            BeneficioOutlinedTextField(
                value = viewModel.descripcion.collectAsState().value,
                onValueChange = { viewModel.descripcion.value = it },
                placeholder = "Descripción breve"
            )

            Etiqueta("Descuento", false)
            BeneficioOutlinedTextField(
                value = viewModel.descuento.collectAsState().value,
                onValueChange = { viewModel.descuento.value = it },
                placeholder = "Ej. 20% o 2x1"
            )

            // 🗓️ Rango de fechas
            RangoFechasPickerE(
                desde = viewModel.desde.collectAsState().value.ifBlank { "dd/MM/yyyy" },
                hasta = viewModel.hasta.collectAsState().value.ifBlank { "dd/MM/yyyy" },
                onDesdeChange = { viewModel.desde.value = it },
                onHastaChange = { fecha, _ -> viewModel.hasta.value = fecha }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 🟣 Botón Guardar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonAction(
                    textoNormal = "Actualizar",
                    textoCargando = "Actualizando...",
                    isLoading = viewModel.isLoading.collectAsState().value,
                    habilitado = true,
                    onClick = {
                        viewModel.actualizarPromocion(
                            idPromocion = idPromocion,
                            context = context,
                            onSuccess = {
                                navController.popBackStack()
                            },
                            onError = { msg ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaDesdePickerE(
    value: String,
    onChange: (String) -> Unit,
    label: String = "Disponible desde",
    modifier: Modifier = Modifier
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = null)

    Etiqueta(label,false,  modifier = Modifier.padding(start = 4.dp))
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
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaHastaPickerE(
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

    Etiqueta(label,false, modifier = Modifier.padding(start = 4.dp))
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RangoFechasPickerE(
    desde: String,
    hasta: String,
    onDesdeChange: (String) -> Unit,
    onHastaChange: (String, Int?) -> Unit,
) {
    val fmt = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    fun parseUtcOrNull(s: String?): LocalDate? =
        try { if (s.isNullOrBlank()) null else LocalDate.parse(s, fmt) } catch (_: Exception) { null }

    var errorHastaMsg by rememberSaveable { mutableStateOf<String?>(null) }



    Column(horizontalAlignment = Alignment.Start, // 👈 Esto alinea a la izquierda
        modifier = Modifier.fillMaxWidth()
    ) {
        FechaDesdePickerE(
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
    Column (horizontalAlignment = Alignment.Start, // 👈 Esto alinea a la izquierda
        modifier = Modifier.fillMaxWidth()
    ){
        FechaHastaPickerE(
            value = hasta,
            minDesde = desde,
            onChange = { fecha, dias ->
                val dDesde = parseUtcOrNull(desde)
                val dHasta = parseUtcOrNull(fecha)
                if (dDesde != null && dHasta != null && dHasta.isBefore(dDesde)) {
                    errorHastaMsg = "La fecha 'Hasta' no puede ser anterior a 'Disponible desde'."
                    return@FechaHastaPickerE
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
