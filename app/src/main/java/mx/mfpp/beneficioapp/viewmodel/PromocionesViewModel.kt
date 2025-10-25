package mx.mfpp.beneficioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.*
import mx.mfpp.beneficioapp.utils.ErrorHandler
/**
 * ViewModel para manejar las promociones de un negocio.
 *
 * Permite cargar las promociones desde el servidor y eliminarlas.
 */
class PromocionesViewModel : ViewModel() {

    /** Lista de promociones cargadas */
    private val _promociones = MutableStateFlow<List<Promocion>>(emptyList())
    val promociones: StateFlow<List<Promocion>> = _promociones

    /** Indica si se está cargando información */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Mensaje de error, si ocurre alguno */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Estado de eliminación de una promoción */
    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting

    /**
     * Carga las promociones de un negocio desde el backend.
     *
     * @param idNegocio ID del negocio cuyas promociones se desean cargar
     */
    fun cargarPromociones(idNegocio: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("PromocionesVM", "🟢 Cargando promociones del negocio ID=$idNegocio")

                val response = ServicioRemotoPromociones.api.getPromocionesPorNegocio(idNegocio)

                if (response.isSuccessful) {
                    val remotas = response.body()?.data ?: emptyList()

                    val locales = remotas.map { p ->
                        Promocion(
                            id = p.id ?: 0,
                            nombre = p.titulo ?: "(Sin título)",
                            descripcion = p.descripcion ?: "",
                            descuento = p.descuento ?: "",
                            categoria = p.estado ?: "General",
                            ubicacion = p.nombreEstablecimiento ?: "Sin ubicación",
                            imagenUrl = p.foto ?: "",
                            expiraEn = p.fechaExpiracion ?: "Sin fecha",
                            esFavorito = false
                        )
                    }

                    _promociones.value = locales
                    Log.d("PromocionesVM", "✅ Se cargaron ${locales.size} promociones")
                } else {
                    _error.value = "Error ${response.code()}: ${response.message()}"
                    Log.e("PromocionesVM", _error.value ?: "")
                }
            } catch (e: Exception) {
                _error.value = ErrorHandler.obtenerMensajeError(e)
                Log.e("PromocionesVM", "❌ Excepción: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina una promoción del backend y de la lista local.
     *
     * @param idPromocion ID de la promoción a eliminar
     * @param onResult Callback que indica si la eliminación fue exitosa y un mensaje
     */
    fun eliminarPromocion(idPromocion: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                _isDeleting.value = true // Mostrar estado “Cargando...”
                val response = ServicioRemotoEliminarPromocion.api.eliminarPromocion(idPromocion)

                if (response.isSuccessful) {
                    _promociones.value = _promociones.value.filter { it.id != idPromocion }
                    onResult(true, "✅ Promoción eliminada correctamente")
                } else {
                    onResult(false, "⚠️ Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "⚠️ Error al eliminar: ${e.localizedMessage}")
            } finally {
                _isDeleting.value = false // Ocultar loading
            }
        }
    }
}