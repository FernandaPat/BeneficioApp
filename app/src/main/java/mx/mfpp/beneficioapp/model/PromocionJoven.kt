// mx.mfpp.beneficioapp.model
package mx.mfpp.beneficioapp.model

data class PromocionJoven(
    val id: Int,
    val titulo_promocion: String,
    val descripcion: String,
    val fecha_creacion: String,
    val fecha_expiracion: String,
    val foto: String?,
    val estado: String,
    val id_establecimiento: Int,
    val nombre_establecimiento: String
)

// CORREGIR: el JSON tiene "data" no "promocionesJoven"
data class PromocionesJovenResponse(
    val data: List<PromocionJoven> // Cambiar aquí
)