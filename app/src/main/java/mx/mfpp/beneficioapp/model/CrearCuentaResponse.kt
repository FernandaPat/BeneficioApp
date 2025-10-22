package mx.mfpp.beneficioapp.model

import com.google.gson.annotations.SerializedName

data class CrearCuentaResponse(

 val id_usuario: String?,
    val nombre: String?,
    val folio_digital: String?,
    val mensaje: String?,
    val status: String?
)
