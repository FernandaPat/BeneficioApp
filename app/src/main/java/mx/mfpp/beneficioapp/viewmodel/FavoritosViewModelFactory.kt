package mx.mfpp.beneficioapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.mfpp.beneficioapp.model.SessionManager
/**
 * Factory para crear instancias de [FavoritosViewModel] con dependencias.
 *
 * Permite inyectar el [SessionManager] necesario para gestionar la sesión del usuario.
 *
 * @param sessionManager Gestión de sesión del usuario.
 */
class FavoritosViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    /**
     * Crea una instancia del ViewModel especificado.
     *
     * @param modelClass Clase del ViewModel a crear.
     * @return Instancia del ViewModel solicitado.
     * @throws IllegalArgumentException si la clase de ViewModel no es [FavoritosViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
            return FavoritosViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
