package mx.mfpp.beneficioapp.view

import mx.mfpp.beneficioapp.R

/**
 * Sistema de navegación sellado que define todas las pantallas de la aplicación.
 *
 * Proporciona rutas, etiquetas e iconos para la navegación tanto para usuarios jóvenes
 * como para negocios.
 *
 * @property ruta Ruta única que identifica la pantalla en el grafo de navegación
 * @property etiqueta Nombre legible para mostrar en la interfaz
 * @property iconoResId Recurso del icono asociado a la pantalla
 */
sealed class Pantalla(
    val ruta: String,
    val etiqueta: String,
    val iconoResId: Int
) {
    /**
     * Objeto companion que contiene constantes y listas de pantallas organizadas.
     */
    companion object {
        /**
         * Lista de pantallas principales para usuarios jóvenes.
         */
        val listaPantallas = listOf(
            BeneficioJovenINICIO,
            BeneficioJovenMAPA,
            BeneficioJovenTARJETA,
            BeneficioJovenBUSCAR,
            BeneficioJovenACTIVIDAD
        )

        /**
         * Lista de pantallas principales para negocios.
         */
        val listaPantallasNegocio = listOf(
            NegocioINICIO,
            NegocioPROMOCIONES,
            NegocioSCANER,
            NegocioPERFIL
        )

        // Rutas constantes para la aplicación de jóvenes
        const val RUTA_JN_APP = "JNPage" //Bienvenida
        const val RUTA_QR_SCANNER_SCREEN = "QRScannerScreen"
        const val RUTA_PERFIL_APP = "PerfilPage"
        const val RUTA_LOGIN_APP = "LoginPage"
        const val RUTA_INICIAR_SESION = "Iniciar_Sesion"
        const val RUTA_RECUPERAR_CONTRASENA = "Recuperar_Contrasena"
        const val RUTA_ACERCADE_APP = "Acerca_De_Page"
        const val RUTA_AYUDA_APP = "Ayuda_Page"
        const val RUTA_CAMBIARCONTRASENA_APP = "CambiarContrasena_Page"
        const val RUTA_DATOSPERSONALES_APP = "VerDatosPersonales_Page"
        const val RUTA_DETALLEPROMOCION_APP = "DetallePromocionScreen"
        const val RUTA_INICIO_APP = "InicioPage" //Joven
        const val RUTA_MAPA_APP = "MapaPage"

        const val RUTA_QR_PROMOCION = "qrPromocion"
        const val RUTA_TARJETA_APP = "TarjetaPage"
        const val RUTA_BUSCAR_APP = "ExplorarPage"
        const val RUTA_ACTIVIDAD_APP = "ActividadPage"
        const val RUTA_SOLICITUD_APP = "SolicitudPage"
        const val RUTA_ESTATUS_SOLICITUD_APP = "EstatusSolicitudPage"
        const val RUTA_NOTIFICACIONES_APP = "NotificacionesPage"
        const val RUTA_CREAR_CUENTA = "Crear_Cuenta"
        const val RUTA_INICIAR_SESION_NEGOCIO = "Iniciar_Sesion_Negocio"
        const val RUTA_AGREGAR_PROMOCIONES = "AgregarPromocion"
        const val RUTA_PROMOCIONES = "Promociones"
        const val RUTA_EDITAR_PROMOCIONES = "editarPromocion/{id}"
        const val RUTA_RESULTADOS_APP = "resultados_app"
        const val RUTA_NEGOCIODETALLE_APP = "NegocioDetallePage"

        const val RUTA_NOTIFICACIONES_NEGOCIO = "notificaciones_negocio"

        /**
         * Ruta parametrizada para resultados con categoría específica.
         */
        const val RUTA_RESULTADOS_CON_CATEGORIA = "resultados_app/{categoria}"

        // Rutas para negocio
        const val RUTA_INICIO_NEGOCIO = "inicio_negocio"
        const val RUTA_PROMOCIONES_NEGOCIO = "promociones_negocio"
        const val RUTA_SCANER_NEGOCIO = "scaner_negocio"
        const val RUTA_PERFIL_NEGOCIO = "perfil_negocio"

        /**
         * Ruta para el historial de escaneos QR.
         */
        const val RUTA_HISTORIAL_SCANNER = "historial_scanner"
    }

    // Pantallas para jóvenes

    /**
     * Pantalla de inicio principal para usuarios jóvenes.
     */
    object BeneficioJovenINICIO: Pantalla(RUTA_INICIO_APP, "Inicio", R.drawable.home)

    /**
     * Pantalla del mapa interactivo.
     */
    object BeneficioJovenMAPA: Pantalla(RUTA_MAPA_APP, "Mapa", R.drawable.map)

    /**
     * Pantalla de la tarjeta digital del usuario.
     */
    object BeneficioJovenTARJETA: Pantalla(RUTA_TARJETA_APP, "Tarjeta", R.drawable.credit_card)

    /**
     * Pantalla de búsqueda y exploración.
     */
    object BeneficioJovenBUSCAR: Pantalla(RUTA_BUSCAR_APP, "Buscar", R.drawable.search)

    /**
     * Pantalla de actividad y notificaciones.
     */
    object BeneficioJovenACTIVIDAD: Pantalla(RUTA_ACTIVIDAD_APP, "Actividad", R.drawable.activity)

    // Pantallas para negocio

    /**
     * Pantalla de inicio para negocios.
     */
    object NegocioINICIO: Pantalla(RUTA_INICIO_NEGOCIO, "Inicio", R.drawable.home)

    /**
     * Pantalla de gestión de promociones para negocios.
     */
    object NegocioPROMOCIONES: Pantalla(RUTA_PROMOCIONES_NEGOCIO, "Promociones", R.drawable.gift)

    /**
     * Pantalla de escáner QR para negocios.
     */
    object NegocioSCANER: Pantalla(RUTA_SCANER_NEGOCIO, "Scanner", R.drawable.camerab)

    /**
     * Pantalla de perfil del negocio.
     */
    object NegocioPERFIL: Pantalla(RUTA_PERFIL_NEGOCIO, "Perfil", R.drawable.user)

    object NegocioNOTIFICACIONES: Pantalla(RUTA_NOTIFICACIONES_NEGOCIO, "Notificaciones", R.drawable.bell)
}