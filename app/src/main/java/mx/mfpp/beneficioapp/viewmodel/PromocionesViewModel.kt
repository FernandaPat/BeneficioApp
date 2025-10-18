package mx.mfpp.beneficioapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.mfpp.beneficioapp.model.Promocion
import mx.mfpp.beneficioapp.network.RetrofitClient
import retrofit2.HttpException
import java.io.IOException

class PromocionesViewModel : ViewModel() {

    // 🔹 Lista de promociones desde la API
    private val _promociones = MutableStateFlow<List<Promocion>>(emptyList())
    val promociones: StateFlow<List<Promocion>> = _promociones.asStateFlow()

    // 🔹 Estados de carga y error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * 🔸 Cargar promociones desde la API según el ID del negocio
     */
    fun cargarPromociones(idNegocio: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = RetrofitClient.api.obtenerPromociones(idNegocio)
                _promociones.value = response.data
            } catch (e: Exception) {
                _error.value = "Error al cargar promociones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * 🔸 Eliminar una promoción del backend y actualizar la lista local
     */
    fun eliminarPromocion(idPromocion: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.eliminarPromocion(idPromocion)
                if (response.isSuccessful) {
                    _promociones.value = _promociones.value.filter { it.id != idPromocion }
                    Log.d("PROMO_DEBUG", "✅ Promoción $idPromocion eliminada correctamente")
                } else {
                    _error.value = "Error al eliminar: ${response.code()}"
                    Log.e("PROMO_DEBUG", "❌ Error al eliminar: ${response.code()}")
                }
            } catch (e: IOException) {
                _error.value = "Error de red: ${e.message}"
                Log.e("PROMO_DEBUG", "❌ Error de red: ${e.message}")
            } catch (e: HttpException) {
                _error.value = "Error del servidor: ${e.message}"
                Log.e("PROMO_DEBUG", "❌ Error del servidor: ${e.message}")
            } catch (e: Exception) {
                _error.value = "Error desconocido: ${e.message}"
                Log.e("PROMO_DEBUG", "❌ Error desconocido: ${e.message}")
            }
        }
    }
}
