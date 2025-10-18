package mx.mfpp.beneficioapp.model

import com.google.android.gms.maps.model.LatLng

data class Lugar(
    val nombre: String,
    val distancia: String,
    val rating: String,
    val coordenadas: LatLng,
    val imagen: String
)