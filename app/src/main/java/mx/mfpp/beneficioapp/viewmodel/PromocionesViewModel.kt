package mx.mfpp.beneficioapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import mx.mfpp.beneficioapp.model.Promocion

class PromocionesViewModel : ViewModel() {
    // Lista de promociones (Simulando datos de backend)
    val promos = mutableStateListOf(
        Promocion(1, "Ropa 10% de descuento", "https://picsum.photos/seed/1/300", "10%", "Comida", 3, "Atizapán", false, 4.5, "Cupon ropa 10%"),
        Promocion(2, "Promoción 2", "https://picsum.photos/seed/2/300", "15%", "Entretenimiento", 0, "CDMX", true, 3.8, "Descripción"),
        Promocion(3, "Promoción 3", null, "20%", "Cine", 5, "CDMX", false, null, "Descripción")
    )

    // Lógica para eliminar promoción
    fun eliminarPromocion(id: Int) {
        val promo = promos.firstOrNull { it.id == id }
        promo?.let { promos.remove(it) }
    }
}
