package mx.mfpp.beneficioapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.mfpp.beneficioapp.model.Promocion

@Composable
fun ActividadPage(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val cupones = remember {
        listOf(
            Promocion(
                1,
                "Cupón 1",
                "https://img1.wsimg.com/isteam/ip/08e934ec-b8b1-481b-9036-1924b6a19e3a/esquites.jpg",
                null,
                "Establecimiento",
                0,
                "",
                false,
                null,
                "Establecimiento"
            ),
            Promocion(
                2,
                "Cupón 2",
                "https://t4.ftcdn.net/jpg/01/89/78/15/360_F_189781543_EpHDnqryinw4fBlU1L3L0YgLdYUxedfi.jpg",
                null,
                "Establecimiento",
                0,
                "",
                false,
                null,
                "Establecimiento"
            ),
            Promocion(
                3,
                "Cupón 3",
                "https://static.vecteezy.com/system/resources/previews/041/281/360/non_2x/pizza-various-flavors-transparent-png.png",
                null,
                "Establecimiento",
                0,
                "",
                false,
                null,
                "Establecimiento"
            )
        )
    }

    Scaffold(
        topBar = {
            TextoTitulo("Actividad reciente")
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.padding(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cupones canjeados",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Box( // 🔸 contenedor para centrar mejor la palabra “Fecha”
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Fecha",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        textAlign = TextAlign.End
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(cupones, key = { _, item -> item.id }) { index, cupon ->
                    ActividadListItem(cupon)
                    if (index < cupones.lastIndex) {
                        Divider(
                            color = Color(0xFFF3F3F3),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActividadListItem(
    cupon: Promocion,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF7F7F7)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = cupon.imagenUrl,
                    contentDescription = cupon.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = cupon.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = cupon.descripcion ?: "Establecimiento",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.LightGray,
                        fontSize = 15.sp
                    )
                )
            }
        }

        Text(
            text = "01/01/0001",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActividadRecientePreview() {
    MaterialTheme {
        val navController = rememberNavController()
        ActividadPage(navController)
    }
}