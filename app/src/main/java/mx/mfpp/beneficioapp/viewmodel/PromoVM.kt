//package mx.mfpp.beneficioapp.viewmodel
//
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import mx.mfpp.beneficioapp.model.Promocion
//
//class PromosVM : ViewModel() {
//    // Simulación: lista ya cargada (cámbialo por repo/DB)
//    private val promos = listOf(
//        Promocion(1, "Promo 1", "https://.../imagen1.jpg", "10%", "Comida", 5, "desc"),
//        // ...
//    )
//
//    private val _promo = MutableStateFlow<Promocion?>(null)
//    val promo: StateFlow<Promocion?> = _promo
//
//    fun loadPromo(id: Int) {
//        _promo.value = promos.find { it.id == id }
//    }
//}
