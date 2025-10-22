package mx.mfpp.beneficioapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.model.ServicioRemotoActualizarPromocion
import mx.mfpp.beneficioapp.model.ServicioRemotoObtenerPromocion
import java.net.URI

class EditarPromocionViewModel : ViewModel() {

    val nombre = MutableStateFlow("")
    val descripcion = MutableStateFlow("")
    val descuento = MutableStateFlow("")
    val desde = MutableStateFlow("")
    val hasta = MutableStateFlow("")
    val imagenUrl = MutableStateFlow("")
    val uri = MutableStateFlow<URI?>(null)
    val isLoading = MutableStateFlow(false)

    /**
     * Cargar datos de la promoción por ID
     */
    fun cargarPromocion(idPromocion: Int, onLoaded: (Promocion?) -> Unit) {
        viewModelScope.launch {
            try {
                println("🟣 Cargando promoción con ID: $idPromocion")
                val promo = ServicioRemotoObtenerPromocion.obtenerPromocionPorId(idPromocion)

                promo?.let {
                    nombre.value = it.nombre
                    descripcion.value = it.descripcion
                    descuento.value = it.descuento
                    desde.value = it.desde
                    hasta.value = it.hasta
                    imagenUrl.value = it.imagenUrl
                    uri.value = try { URI(it.imagenUrl) } catch (_: Exception) { null }

                    println("✅ Promoción cargada: ${it.nombre}")
                } ?: println("⚠️ No se encontró la promoción con ID: $idPromocion")

                onLoaded(promo)
            } catch (e: Exception) {
                println("❌ Error al cargar promoción: ${e.message}")
                onLoaded(null)
            }
        }
    }

    /**
     * Actualizar datos de la promoción en el servidor
     */
    fun actualizarPromocion(
        idPromocion: Int,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                println("🟣 Actualizando promoción con ID: $idPromocion")

                val promoActualizada = Promocion(
                    id = idPromocion,
                    nombre = nombre.value,
                    descripcion = descripcion.value,
                    descuento = descuento.value,
                    desde = desde.value,
                    hasta = hasta.value,
                    imagenUrl = imagenUrl.value
                )

                val exito = ServicioRemotoActualizarPromocion.actualizarPromocion(
                    idPromocion = idPromocion,
                    promocion = promoActualizada
                )

                if (exito) {
                    println("✅ Promoción actualizada correctamente.")
                    onSuccess()
                } else {
                    onError("⚠️ Error al actualizar la promoción.")
                }
            } catch (e: Exception) {
                onError("❌ Excepción al actualizar promoción: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
}
