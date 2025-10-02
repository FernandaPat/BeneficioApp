package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.mfpp.beneficioapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificacionPage(navController: NavController) {
    Scaffold(
        topBar = { ArrowTopBar(navController, "Notificaciones") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color(0xFF230448))
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Expira",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)

            )

            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
            NotificationItem("Cupón 1", "01/01/0001", "Establecimiento")
        }
    }
}


@Composable
fun NotificationItem(cupon: String, fecha: String, establecimiento: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(106.dp)
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenCupon()

            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = cupon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = establecimiento,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = fecha,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun ImagenCupon() {
    Card(
        modifier = Modifier
            .size(80.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Img",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun notificacionPreview() {
    val navController = rememberNavController()
    NotificacionPage(navController)
}