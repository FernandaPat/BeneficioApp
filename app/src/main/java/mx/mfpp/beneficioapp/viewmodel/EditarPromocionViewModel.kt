package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.ServicioRemotoActualizarPromocion
import mx.mfpp.beneficioapp.model.ServicioRemotoPromocionPorId
import mx.mfpp.beneficioapp.utils.convertirImagenABase64
import java.time.LocalDate
import java.time.format.DateTimeFormatter



class EditarPromocionViewModel : ViewModel() {

    // 🧾 Campos editables
    var nombre = MutableStateFlow("")
    var descripcion = MutableStateFlow("")
    var descuento = MutableStateFlow("")
    var desde = MutableStateFlow("")
    var hasta = MutableStateFlow("")

    // 📸 Imagen
    var nuevaImagenUri = MutableStateFlow<Uri?>(null)
    var imagenRemota = MutableStateFlow<String?>(null)

    // ⚙️ Estado general
    var isLoading = MutableStateFlow(false)

    // === ACTUALIZADORES ===
    fun actualizarNombre(v: String) { nombre.value = v }
    fun actualizarDescripcion(v: String) { descripcion.value = v }
    fun actualizarDescuento(v: String) { descuento.value = v }
    fun actualizarDesde(v: String) { desde.value = v }
    fun actualizarHasta(v: String) { hasta.value = v }
    fun actualizarImagen(uri: Uri?) { nuevaImagenUri.value = uri }

    // === CARGAR PROMOCIÓN EXISTENTE ===
    fun cargarPromocionPorId(idPromocion: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val promocion = ServicioRemotoPromocionPorId.obtenerPromocionPorId(idPromocion)
                println("📡 Datos recibidos: $promocion")

                nombre.value = promocion.titulo ?: ""
                descripcion.value = promocion.descripcion ?: ""
                descuento.value = promocion.descuento ?: ""
                desde.value = promocion.disponible_desde ?: ""
                hasta.value = promocion.hasta ?: ""
                imagenRemota.value = promocion.imagen ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    // === ACTUALIZAR PROMOCIÓN ===
    fun actualizarPromocion(
        context: Context,
        idPromocion: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // 🔹 Convierte imagen nueva (si hay)
                val imagenBase64 = nuevaImagenUri.value?.let { convertirImagenABase64(context, it) } ?: imagenRemota.value

                val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                fun normalizarFecha(fecha: String): String {
                    // Intenta convertir fechas tipo 2025-10-23 → 23/10/2025
                    return try {
                        if (fecha.contains("-")) {
                            LocalDate.parse(fecha).format(formato)
                        } else fecha // ya está en dd/MM/yyyy
                    } catch (e: Exception) {
                        fecha
                    }
                }

                val ok = ServicioRemotoActualizarPromocion.actualizarPromocion(
                    idPromocion = idPromocion,
                    titulo = nombre.value,
                    descripcion = descripcion.value,
                    descuento = descuento.value,
                    disponibleDesde = normalizarFecha(desde.value),
                    hasta = normalizarFecha(hasta.value),
                    imagenBase64 = imagenBase64
                )


                if (ok) onSuccess()
                else onError("Error al actualizar la promoción")

            } catch (e: Exception) {
                e.printStackTrace()
                onError("Error al actualizar la promoción")
            } finally {
                isLoading.value = false
            }
        }
    }
}
