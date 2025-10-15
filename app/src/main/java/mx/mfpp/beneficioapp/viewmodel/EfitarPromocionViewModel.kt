package mx.mfpp.beneficioapp.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import mx.mfpp.beneficioapp.model.Promocion

class EditarPromocionViewModel : ViewModel() {

    // 🔹 Estado actual de la promoción
    var promocion = mutableStateOf(
        Promocion(
            id = 1,
            nombre = "Descuento en Ropa",
            imagenUrl = "https://picsum.photos/300",
            descuento = "10%",
            categoria = "Moda",
            expiraEn = 5,
            ubicacion = "Atizapán",
            esFavorito = false,
            rating = 4.2,
            descripcion = "Aprovecha este descuento especial en ropa"
        )
    )
        private set

    // 🔹 Imagen seleccionada localmente
    var nuevaImagenUri = mutableStateOf<Uri?>(null)
        private set

    // 🔹 Métodos para actualizar campos individuales
    fun actualizarNombre(nuevo: String) {
        promocion.value = promocion.value.copy(nombre = nuevo)
    }

    fun actualizarDescripcion(nuevo: String) {
        promocion.value = promocion.value.copy(descripcion = nuevo)
    }

    fun actualizarDescuento(nuevo: String) {
        promocion.value = promocion.value.copy(descuento = nuevo)
    }

    fun actualizarCategoria(nueva: String) {
        promocion.value = promocion.value.copy(categoria = nueva)
    }

    fun actualizarExpiraEn(dias: Int?) {
        promocion.value = promocion.value.copy(expiraEn = dias)
    }

    fun actualizarImagen(uri: Uri?) {
        nuevaImagenUri.value = uri
    }

    // 🔹 Simular guardado
    fun guardarCambios(): Boolean {
        // Aquí podrías conectar con tu repositorio o backend
        println("✅ Promoción actualizada: ${promocion.value}")
        return true
    }
}
