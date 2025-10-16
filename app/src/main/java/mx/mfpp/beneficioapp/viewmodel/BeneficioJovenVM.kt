package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel principal que coordina los demás ViewModels
 * Mantiene solo el estado global y la coordinación entre módulos
 */
class BeneficioJovenVM : ViewModel() {

    private val _estadoCargandoGlobal = MutableStateFlow(false)
    val estadoCargandoGlobal: StateFlow<Boolean> = _estadoCargandoGlobal.asStateFlow()

    private val _errorGlobal = MutableStateFlow<String?>(null)
    val errorGlobal: StateFlow<String?> = _errorGlobal.asStateFlow()

    /**
     * Carga todos los datos iniciales de la aplicación
     */
    fun cargarDatosIniciales() {
        _estadoCargandoGlobal.value = true
        _errorGlobal.value = null

        viewModelScope.launch {
            try {
                // Aquí podrías coordinar la carga de datos de todos los ViewModels
                // Por ahora simulamos una carga global
                kotlinx.coroutines.delay(1000)
            } catch (e: Exception) {
                _errorGlobal.value = "Error al cargar los datos iniciales: ${e.message}"
            } finally {
                _estadoCargandoGlobal.value = false
            }
        }
    }

    /**
     * Recarga todos los datos de la aplicación
     */
    fun refrescarDatos() {
        cargarDatosIniciales()
    }

    /**
     * Limpia cualquier mensaje de error global
     */
    fun limpiarErrorGlobal() {
        _errorGlobal.value = null
    }
}