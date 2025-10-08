package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

data class Categoria(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val nombre: String,

    @SerializedName("icon")
    val icono: String,

    @SerializedName("color")
    val color: String
)
