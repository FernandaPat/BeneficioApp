package mx.mfpp.beneficioapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.PromocionRequest
import mx.mfpp.beneficioapp.network.RetrofitClient
import java.util.*

class AgregarPromocionViewModel : ViewModel() {

    // Campos de la UI
    val uri = mutableStateOf<Uri?>(null)
    val nombre = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val descuento = mutableStateOf("")
    val categoria = mutableStateOf("")
    val desde = mutableStateOf("")
    val hasta = mutableStateOf("")
    val expiraEn = mutableStateOf<Int?>(null)
    val categorias = listOf("Alimentos", "Ropa", "Entretenimiento", "Servicios", "Salud")

    /**
     * Envía la promoción a la API.
     */
    fun guardarPromocion(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // ⚠️ En tu app deberías convertir la imagen URI a Base64 real.
                val imagenBase64 = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/..."

                val promocion = PromocionRequest(
                    id_negocio = 6, // puedes hacerlo dinámico luego
                    titulo = nombre.value,
                    descripcion = descripcion.value,
                    descuento = descuento.value,
                    disponible_desde = desde.value,
                    hasta = hasta.value,
                    imagen = imagenBase64
                )

                val response = RetrofitClient.api.registrarPromocion(promocion)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al registrar promoción: ${response.code()}")
                    Log.e("API", "Código: ${response.code()}, mensaje: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
                Log.e("API", "Excepción: ${e.localizedMessage}")
            }
        }
    }
}
