package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que maneja el estado global de la aplicación para jóvenes.
 *
 * Gestiona la carga inicial de datos, refresco y manejo de errores globales.
 */
class BeneficioJovenVM : ViewModel() {
    /**
     * Estado que indica si hay una operación de carga global en progreso.
     */
    private val _estadoCargandoGlobal = MutableStateFlow(false)
    /**
     * Exposición de solo lectura del estado de carga global.
     */
    val estadoCargandoGlobal: StateFlow<Boolean> = _estadoCargandoGlobal.asStateFlow()
    /**
     * Estado que contiene el mensaje de error global, si existe alguno.
     */
    private val _errorGlobal = MutableStateFlow<String?>(null)
    /**
     * Exposición de solo lectura del error global.
     */
    val errorGlobal: StateFlow<String?> = _errorGlobal.asStateFlow()
    /**
     * Carga todos los datos iniciales de la aplicación.
     *
     * Marca el estado de carga global como `true` y limpia cualquier error previo.
     * Luego simula la carga de datos de todos los ViewModels y actualiza el estado de carga.
     * En caso de error, se actualiza [errorGlobal] con el mensaje correspondiente.
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