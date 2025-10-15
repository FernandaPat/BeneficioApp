package mx.mfpp.beneficioapp.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import mx.mfpp.beneficioapp.model.Promocion

/**
 * ViewModel encargado de manejar el estado y lógica
 * de la pantalla de "Agregar Promociones".
 */
class AgregarPromocionViewModel : ViewModel() {

    // 📸 Imagen seleccionada
    val uri = mutableStateOf<Uri?>(null)

    // 📝 Campos de texto
    val nombre = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val descuento = mutableStateOf("")
    val categoria = mutableStateOf("")
    val desde = mutableStateOf("")
    val hasta = mutableStateOf("")
    val expiraEn = mutableStateOf<Int?>(null)

    // 🔽 Lista de categorías disponibles
    val categorias = listOf(
        "Belleza", "Comida", "Educación",
        "Salud", "Entretenimiento", "Moda", "Servicios"
    )

    // ✅ Simulación de guardar promoción (puedes conectar al backend después)
    fun guardarPromocion(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (nombre.value.isBlank() || descripcion.value.isBlank() || categoria.value.isBlank()) {
            onError("Por favor completa todos los campos obligatorios.")
            return
        }

        val nuevaPromo = Promocion(
            id = (0..10000).random(),
            nombre = nombre.value,
            imagenUrl = uri.value?.toString(),
            descuento = descuento.value,
            categoria = categoria.value,
            expiraEn = expiraEn.value,
            ubicacion = "Atizapán",
            esFavorito = false,
            rating = null,
            descripcion = descripcion.value
        )

        // Simula guardado exitoso (aquí podrías hacer tu POST al backend)
        println("✅ Promoción agregada: $nuevaPromo")

        // Limpiar campos tras guardar
        limpiarCampos()
        onSuccess()
    }

    // 🧹 Limpia los campos después de guardar
    private fun limpiarCampos() {
        uri.value = null
        nombre.value = ""
        descripcion.value = ""
        descuento.value = ""
        categoria.value = ""
        desde.value = ""
        hasta.value = ""
        expiraEn.value = null
    }
}
