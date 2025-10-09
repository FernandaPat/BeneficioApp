package mx.mfpp.beneficioapp.view

import mx.mfpp.beneficioapp.R

sealed class Pantalla(
    val ruta: String,
    val etiqueta: String,
    val iconoResId: Int
) {
    companion object {
        val listaPantallas = listOf(
            BeneficioJovenINICIO,
            BeneficioJovenMAPA,
            BeneficioJovenTARJETA,
            BeneficioJovenBUSCAR,
            BeneficioJovenACTIVIDAD
        )

        const val RUTA_JN_APP = "JNPage"
        const val RUTA_LOGIN_APP = "LoginPage"
        const val RUTA_INICIAR_SESION = "Iniciar_Sesion"
        const val RUTA_INICIO_APP = "InicioPage"
        const val RUTA_MAPA_APP = "MapaPage"
        const val RUTA_TARJETA_APP = "TarjetaPage"
        const val RUTA_BUSCAR_APP = "BuscarPage"
        const val RUTA_ACTIVIDAD_APP = "ActividadPage"
        const val RUTA_SOLICITUD_APP = "SolicitudPage"
        const val RUTA_ESTATUS_SOLICITUD_APP = "EstatusSolicitudPage"
        const val RUTA_NOTIFICACIONES_APP = "NotificacionesPage"
        const val RUTA_CREAR_CUENTA = "Crear_Cuenta"
        const val RUTA_INICIAR_SESION_NEGOCIO = "Iniciar_Sesion_Negocio"
        const val RUTA_AGREGAR_PROMOCIONES = "Agregar_Promociones"
        const val RUTA_PROMOCIONES = "Promociones"
        const val RUTA_EDITAR_PROMOCIONES = "Editar_Promociones"
    }
    object BeneficioJovenINICIO:
        Pantalla(RUTA_INICIO_APP, "Inicio", R.drawable.home)
    object BeneficioJovenMAPA:
        Pantalla(RUTA_MAPA_APP, "Mapa", R.drawable.map)
    object BeneficioJovenTARJETA:
        Pantalla(RUTA_TARJETA_APP, "Tarjeta", R.drawable.credit_card)
    object BeneficioJovenBUSCAR:
        Pantalla(RUTA_BUSCAR_APP, "Buscar", R.drawable.search)
    object BeneficioJovenACTIVIDAD:
        Pantalla(RUTA_ACTIVIDAD_APP, "Actividad", R.drawable.activity)

}
