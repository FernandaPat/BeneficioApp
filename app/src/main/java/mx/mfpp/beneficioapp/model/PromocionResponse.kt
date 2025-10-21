package mx.mfpp.beneficioapp.model

import PromocionRemota
import com.google.gson.annotations.SerializedName

// 🔹 Respuesta general del backend

data class PromocionesResponse(
    val status: String?,
    val data: List<PromocionRemota>?
)