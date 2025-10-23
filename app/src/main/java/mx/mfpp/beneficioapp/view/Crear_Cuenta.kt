package mx.mfpp.beneficioapp.view

import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.mfpp.beneficioapp.viewmodel.CrearCuentaViewModel
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

/**
 * Pantalla para el registro de una nueva cuenta en la aplicación Beneficio Joven.
 *
 * Incluye campos para capturar información personal, dirección, género, correo, teléfono,
 * contraseña y datos de la tarjeta física, en caso de tenerla. Además, valida la contraseña
 * y permite navegar hacia la pantalla de inicio de sesión.
 *
 * @param navController Controlador de navegación para dirigir entre pantallas.
 * @param modifier Modificador opcional para ajustar el diseño de la vista.
 */
@Composable
fun Crear_Cuenta(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CrearCuentaViewModel = viewModel()
) {
    val usuario = viewModel.usuario.value
    val scroll = rememberScrollState()
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }


    Scaffold(
        topBar = { ArrowTopBar(navController, "Crear Cuenta") },
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(padding)
                .imePadding()
        ) {
            // === CAMPOS PERSONALES ===
            Etiqueta("Nombre(s)", true)
            CapturaTexto("Escribe aquí", value = usuario.nombre, onValueChange = viewModel::onNombreChange,30)

            Etiqueta("Apellido Paterno", true)
            CapturaTexto("Escribe aquí", value = usuario.apellidoPaterno, onValueChange = viewModel::onApellidoPaternoChange,30)

            Etiqueta("Apellido Materno", true)
            CapturaTexto("Escribe aquí", value = usuario.apellidoMaterno, onValueChange = viewModel::onApellidoMaternoChange,30)

            Etiqueta("CURP", true)
            CapturaTexto("CURP", value = usuario.curp, onValueChange = viewModel::onCurpChange,18)

            Etiqueta("Fecha de Nacimiento", true)
            Fecha_nacimiento(onFechaChange = viewModel::onFechaChange)

            Etiqueta("Género", true)
            SeleccionarGenero(onSelected = viewModel::onGeneroChange)

            // === DIRECCIÓN ===
            SeccionDireccion(onAddressChange = viewModel::onDireccionChange)

            // === CONTACTO ===
            Etiqueta("Correo", true)
            CapturaTexto("correo@ejemplo.com", value = usuario.correo, onValueChange = viewModel::onCorreoChange,40)

            Etiqueta("Teléfono", true)
            CapturaNumeroTelefono("10 dígitos", value = usuario.celular, onValueChange = viewModel::onTelefonoChange,10)

            // === CONTRASEÑA ===
            Etiqueta("Contraseña", true)
            BeneficioPasswordField(
                value = usuario.password,
                onValueChange = viewModel::onContrasenaChange,
                placeholder = "Mín. 8 caracteres"
            )
            PasswordChecklist(usuario.password)

            // === TARJETA ===
            Etiqueta("¿Cuentas con una tarjeta física?", true)
            Tarjeta(
                seleccion = usuario.tieneTarjeta,
                onSelected = viewModel::onTieneTarjetaChange
            )

            if (usuario.tieneTarjeta == true) {
                Etiqueta("Número de folio", true)
                BeneficioOutlinedTextField(
                    value = usuario.folio_antiguo ?: "",
                    onValueChange = { nuevoValor ->
                        if (nuevoValor.length <= 16) {
                            viewModel.onFolioChange(nuevoValor)
                        }
                    },
                    placeholder = "1234 5678 9012 3456",
                    maxLength = 18,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }
            Spacer(Modifier.height(25.dp))
            // === CONSENTIMIENTO ===
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
            ) {
                Checkbox(
                    checked = usuario.consentimientoAceptado,
                    onCheckedChange = { checked ->
                        if (!usuario.consentimientoAceptado && checked) {
                            mostrarDialogo = true // muestra el diálogo solo si intenta aceptarlo
                        } else {
                            viewModel.onConsentimientoChange(false) // si lo desmarca manualmente
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFE9d4ff),      // color de la palomita cuando está marcado
                        checkmarkColor = Color(0xFF9605f7),    // color de la palomita misma si quieres más contraste
                        uncheckedColor = Color(0xFFE9d4ff)     // color del borde cuando no está marcado
                    )
                )
                Text(
                    buildAnnotatedString {
                        append("He leído y acepto los Términos y Condiciones ")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                            append("*")
                        }
                    },
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { mostrarDialogo = true }
                )
            }

            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                ButtonAction(
                    textoNormal = "Crear Cuenta",
                    textoCargando = "Creando...",
                    isLoading = viewModel.isLoading.value,
                    habilitado = viewModel.esFormularioValido(),
                    onClick = {
                        Log.d("CrearCuenta", "Botón 'Crear Cuenta' presionado")

                        if (viewModel.esFormularioValido()) {
                            viewModel.isLoading.value = true
                            viewModel.registrarUsuario(context) { exito, mensajeError ->
                                coroutineScope.launch {
                                    viewModel.isLoading.value = false
                                    if (exito) {
                                        Log.d("CrearCuenta", "Cuenta creada exitosamente, navegando...")
                                        navController.navigate(Pantalla.RUTA_INICIAR_SESION) {
                                            popUpTo(Pantalla.RUTA_CREAR_CUENTA) { inclusive = true }
                                        }
                                    } else {
                                        snackbarHostState.showSnackbar(
                                            mensajeError ?: "Error desconocido al crear la cuenta"
                                        )
                                    }
                                }
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Por favor completa todos los campos obligatorios.")
                            }
                        }
                    }
                )
            }
            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
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
            Spacer(Modifier.height(60.dp))
        }
    }
    // --- Diálogo modular de Términos ---
    TerminosDialog(
        mostrar = mostrarDialogo,
        onAceptar = {
            viewModel.onConsentimientoChange(true)
            mostrarDialogo = false
        },
        onCancelar = {
            viewModel.onConsentimientoChange(false)
            mostrarDialogo = false
        }
    )
}
/**
 * Extensión de [Modifier] para aplicar un estilo de entrada uniforme
 * en los campos de texto del sistema Beneficio Joven.
 */
fun Modifier.beneficioInput(): Modifier = this
    .padding(start = 20.dp, end = 20.dp, bottom = 8.dp, top = 13.dp)
    .width(380.dp)
    .height(53.dp)
/**
 * Sección para capturar los datos completos de dirección del usuario.
 *
 * Incluye campos para calle, número exterior/interior, colonia, código postal,
 * municipio y estado. Cada cambio en los campos actualiza un objeto [Direccion].
 *
 * @param modifier Modificador opcional para el diseño del contenedor.
 * @param onAddressChange Callback que devuelve la dirección actualizada.
 */
@Composable
fun TerminosDialog(
    mostrar: Boolean,
    onAceptar: () -> Unit,
    onCancelar: () -> Unit
) {
    if (mostrar) {
        Dialog(onDismissRequest = { onCancelar() }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Términos y Condiciones de Uso – Atizapp",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9605F7)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Última actualización: octubre 2025",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(16.dp))

                    // === Contenido completo seccionado ===
                    Text(
                        text = """
Bienvenido(a) a Atizapp, una aplicación desarrollada en colaboración con el Municipio de Atizapán de Zaragoza con el propósito de ofrecer beneficios, descuentos y promociones a jóvenes registrados dentro del programa Beneficio Joven. 
Al utilizar esta aplicación, usted acepta los siguientes Términos y Condiciones. 
Si no está de acuerdo con alguno de ellos, deberá abstenerse de usar la plataforma.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text("1. Definiciones", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
• Atizapp: Plataforma móvil que digitaliza la Tarjeta Beneficio Joven.
• Usuario Joven: Persona de entre 12 y 29 años registrada en el programa Beneficio Joven.
• Usuario Negocio: Establecimiento o comercio afiliado que ofrece beneficios a los jóvenes registrados.
• Administrador: Personal autorizado por el Municipio para supervisar, validar y gestionar la aplicación.
                    """.trimIndent())

                    Text("2. Condiciones de Uso", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
• El uso de Atizapp es gratuito para los jóvenes y negocios participantes.
• El usuario se compromete a utilizar la aplicación de manera responsable, lícita y conforme a estos Términos y Condiciones.
• El acceso a los beneficios está condicionado al registro con datos verídicos y validación del CURP.
• Cada usuario tendrá una cuenta única, personal e intransferible.
                    """.trimIndent())

                    Text("3. Registro y Autenticación", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
• El usuario deberá proporcionar datos personales verídicos.
• La información será protegida conforme a la Ley General de Protección de Datos Personales.
• Atizapp no solicitará contraseñas por medios distintos a la propia aplicación.
                    """.trimIndent())

                    Text("4. Promociones y Beneficios", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
• Las promociones publicadas son responsabilidad de los comercios afiliados.
• El Municipio y Atizapp no se hacen responsables por la disponibilidad o cumplimiento de las promociones.
• El usuario podrá redimir beneficios mediante la lectura de su código QR en el establecimiento correspondiente.
                    """.trimIndent())

                    Text("5. Privacidad y Protección de Datos", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
• Atizapp recopila únicamente la información necesaria para su funcionamiento.
• Los datos personales no serán vendidos ni transferidos sin autorización expresa.
• El usuario puede solicitar la eliminación de sus datos enviando un correo al área responsable del programa Beneficio Joven.
                    """.trimIndent())

                    Text("6. Uso Indebido y Sanciones", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
• Se prohíbe el uso de la aplicación para fines fraudulentos o ilegales.
• Cualquier intento de manipulación del sistema o uso de identidades falsas podrá resultar en la suspensión de la cuenta.
                    """.trimIndent())

                    Text("7. Limitación de Responsabilidad", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
• Atizapp no se responsabiliza por fallos técnicos, interrupciones del servicio o pérdida de información ocasionada por factores externos.
• El Municipio y los desarrolladores no garantizan la disponibilidad continua de la plataforma.
                    """.trimIndent())

                    Text("8. Propiedad Intelectual", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
• Todos los derechos sobre el diseño, código fuente, logotipos y contenido pertenecen al Municipio de Atizapán o sus desarrolladores autorizados.
• Queda prohibida la reproducción total o parcial sin consentimiento previo por escrito.
                    """.trimIndent())

                    Text("9. Modificaciones", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
• Los presentes Términos y Condiciones podrán ser modificados en cualquier momento.
• Las actualizaciones se notificarán a través de la aplicación o en el sitio web oficial del Municipio.
                    """.trimIndent())

                    Text("10. Contacto", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Text("""
Para dudas, comentarios o solicitudes relacionadas con estos Términos y Condiciones, puede comunicarse a:
beneficiojoven@atizapan.gob.mx
Dirección de Juventud – Municipio de Atizapán de Zaragoza
                    """.trimIndent(), textAlign = TextAlign.Start)

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Botones ---
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { onCancelar() }) {
                            Text("Cancelar", color = Color.Gray)
                        }
                        Spacer(Modifier.width(12.dp))
                        Button(
                            onClick = { onAceptar() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE9D4FF)
                            )
                        ) {
                            Text(
                                text = "Aceptar",
                                color = Color(0xFF9605F7),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
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
/**
 * Selector binario visual para indicar si el usuario cuenta con tarjeta física.
 *
 * @param seleccion Valor actual de selección (true, false o null).
 * @param onSelected Callback al seleccionar una opción.
 * @param modifier Modificador opcional.
 */
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
/**
 * Botón de selección en forma de "pill" (opción redondeada).
 *
 * Se utiliza en componentes de selección binaria o múltiple.
 *
 * @param text Texto que se muestra dentro del botón.
 * @param selected Indica si la opción está activa.
 * @param onClick Acción al hacer clic sobre la opción.
 */
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
/**
 * Menú desplegable para seleccionar el estado de residencia.
 *
 * Si se selecciona "Otro", muestra una advertencia indicando
 * que los beneficios aplican únicamente para Atizapán.
 *
 * @param onSelected Callback que devuelve el estado elegido.
 * @param modifier Modificador opcional.
 */
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
/**
 * Selector de fecha de nacimiento compuesto por tres menús desplegables:
 * día, mes y año. Actualiza los valores conforme se seleccionan.
 *
 * @param yearRange Rango de años disponibles (por defecto 1900 hasta año actual).
 * @param onFechaChange Callback con día, mes y año seleccionados.
 */
@Composable
fun Fecha_nacimiento(
    modifier: Modifier = Modifier,
    yearRange: IntRange = (1995..Calendar.getInstance().get(Calendar.YEAR)),
    onFechaChange: (Int?, Int?, Int?) -> Unit = { _, _, _ -> }
) {
    FechaNacimientoDropdowns(
        modifier = modifier,
        yearRange = yearRange,
        onDateSelected = onFechaChange
    )
    modifier.beneficioInput()
}
/**
 * Subcomponente para renderizar los menús desplegables de fecha de nacimiento.
 *
 * Calcula dinámicamente el número de días según el mes y año seleccionados.
 *
 * @param yearRange Rango de años válidos.
 * @param onDateSelected Callback que devuelve día, mes y año actualizados.
 */
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
/**
 * Campo genérico para selección de números (día, mes o año) mediante un menú desplegable.
 *
 * @param placeholder Texto indicativo cuando no hay valor seleccionado.
 * @param range Rango de números seleccionables.
 * @param selected Valor actual seleccionado.
 * @param onSelected Callback cuando se elige un nuevo número.
 */
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
/**
 * Texto gris utilizado como placeholder en los campos personalizados.
 *
 * @param text Texto que se mostrará como ejemplo.
 */
@Composable
fun BeneficioPlaceholder(text: String) {
    Text(
        text = text,
        color = Color(0xFFA1A0A0),
        style = LocalTextStyle.current.copy(fontSize = 14.sp, lineHeight = 20.sp),

        )
}

/**
 * Campo de texto con borde y formato adaptado al estilo de la aplicación.
 *
 * @param value Valor actual del campo.
 * @param onValueChange Callback que actualiza el valor.
 * @param placeholder Texto guía que se muestra dentro del campo.
 * @param keyboardOptions Configuración del teclado (tipo, acción IME, etc.).
 * @param readOnly Si es true, el campo no puede editarse.
 */
@Composable
fun BeneficioOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxLength: Int? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false,
    isError: Boolean = false // 🔹 Agregado aquí
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
/**
 * Campo de contraseña con opción de mostrar u ocultar el texto.
 *
 * @param value Contraseña actual escrita.
 * @param onValueChange Callback que actualiza el valor.
 * @param placeholder Texto guía (por ejemplo “Mín. 8 caracteres”).
 */
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
/**
 * Lista visual de requisitos de contraseña.
 *
 * Verifica longitud, mayúsculas, números y caracteres especiales.
 *
 * @param password Contraseña actual a validar.
 */
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

/**
 * Elemento individual de la lista de verificación de contraseña.
 *
 * Muestra un ícono e indica visualmente si la condición se cumple.
 *
 * @param texto Texto descriptivo del requisito.
 * @param activo Indica si la regla se cumple.
 * @param okColor Color cuando la regla está activa.
 * @param idleColor Color cuando no se cumple.
 */
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

/**
 * Campo de entrada para capturar un número telefónico de 10 dígitos.
 *
 * @param placeholder Texto guía del campo.
 */
@Composable
fun CapturaNumeroTelefono(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int? = null,
    modifier: Modifier = Modifier
) {
    BeneficioOutlinedTextField(
        value = value,
        onValueChange = { input -> onValueChange(input.filter { it.isDigit() }.take(10)) },
        placeholder = placeholder,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}

/**
 * Campo de texto genérico para capturar texto libre.
 *
 * @param placeholder Texto guía mostrado en el campo.
 * @param maxLength Longitud máxima permitida.
 */
@Composable
fun CapturaTexto(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int? = null,
    modifier: Modifier = Modifier
) {
    BeneficioOutlinedTextField(
        value = value,
        onValueChange = { input -> onValueChange(maxLength?.let { input.take(it) } ?: input) },
        placeholder = placeholder,
        modifier = modifier
    )
}


/**
 * Etiqueta superior que acompaña cada campo de texto.
 *
 * Si el campo es obligatorio, muestra un asterisco rojo.
 *
 * @param texto Texto principal de la etiqueta.
 * @param obligatorio Indica si el campo es requerido.
 */
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
/**
 * Menú desplegable para seleccionar el género del usuario.
 *
 * @param options Lista de opciones disponibles (por defecto: Femenino, Masculino, Otro).
 * @param onSelected Callback con la opción elegida.
 */
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
                        expanded = false
                        val dbValue = when (option) {
                            "Femenino" -> "F"
                            "Masculino" -> "M"
                            else -> "Otro"
                        }
                        onSelected(dbValue)
                    }
                )
            }
        }
    }
}

/**
 * Vista previa del formulario de creación de cuenta.
 *
 * Permite visualizar el diseño completo dentro del editor de Compose.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CrearCuentaPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        Crear_Cuenta(navController)
    }
}