package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.ServicioRemotoActualizarPromocion
import mx.mfpp.beneficioapp.model.Promocion

class EditarPromocionViewModel : ViewModel() {

    val nombre = MutableStateFlow("")
    val descripcion = MutableStateFlow("")
    val descuento = MutableStateFlow("")
    val desde = MutableStateFlow("")
    val hasta = MutableStateFlow("")
    val imagenUrl = MutableStateFlow("")

    val uri = mutableStateOf<Uri?>(null)


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun actualizarPromocion(
        idPromocion: Int,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        println("🟣 Actualizando promoción con ID: $idPromocion")

        if (idPromocion <= 0) {
            onError("❌ ID de promoción inválido ($idPromocion)")
            return
        }

        val promo = Promocion(
            id = idPromocion,
            nombre = nombre.value,
            descripcion = descripcion.value,
            descuento = descuento.value,
            desde = desde.value,
            hasta = hasta.value,
            imagenUrl = imagenUrl.value
        )

        viewModelScope.launch {
            _isLoading.value = true
            val (exito, mensaje) = ServicioRemotoActualizarPromocion.actualizarPromocion(idPromocion, promo)
            _isLoading.value = false

            if (exito) onSuccess() else onError(mensaje)
        }
    }
}
