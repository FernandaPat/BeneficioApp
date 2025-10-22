package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TerminosYCondicionesPage(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        // --- Título ---
        Text(
            text = "Términos y Condiciones de Uso – Atizapp",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "Última actualización: octubre 2025",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // --- Contenido principal ---
        Text(
            text = """
Bienvenido(a) a Atizapp, una aplicación desarrollada en colaboración con el Municipio de Atizapán de Zaragoza con el propósito de ofrecer beneficios, descuentos y promociones a jóvenes registrados dentro del programa Beneficio Joven. 
Al utilizar esta aplicación, usted acepta los siguientes Términos y Condiciones. 
Si no está de acuerdo con alguno de ellos, deberá abstenerse de usar la plataforma.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // --- Secciones ---
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
📧 beneficiojoven@atizapan.gob.mx
🏛️ Dirección de Juventud – Municipio de Atizapán de Zaragoza
        """.trimIndent(), textAlign = TextAlign.Start)

        Spacer(modifier = Modifier.height(32.dp))

        // --- Botón Aceptar ---
        Button(
            onClick = {
                // 🔹 Regresa a la pantalla anterior
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                buildAnnotatedString {
                    append("Aceptar")

                },
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}
