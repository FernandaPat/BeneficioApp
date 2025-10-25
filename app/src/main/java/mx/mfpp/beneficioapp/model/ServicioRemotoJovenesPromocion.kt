// mx.mfpp.beneficioapp.model.ServicioRemotoJovenesPromocion
package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.delay

/**
 * Objeto (Singleton) que gestiona la comunicación con la API remota
 * (Cloud Function) para obtener la lista de promociones para jóvenes.
 *
 * Este servicio se encarga de:
 * 1. Configurar Retrofit para el endpoint específico [URL_BASE].
 * 2. Implementar una lógica de paginación para descargar *todas* las promociones,
 * no solo una página.
 * 3. Implementar un mecanismo de reintentos ([MAX_REINTENTOS]) con backoff simple
 * para manejar fallos de red temporales.
 */
object ServicioRemotoJovenesPromocion {
    /** URL base del servicio (Cloud Function) para listar promociones. */
    private const val URL_BASE = "https://listar-promociones-819994103285.us-central1.run.app/"
    /** Número máximo de reintentos para una solicitud de página fallida. */
    private const val MAX_REINTENTOS = 3

    /**
     * Instancia [Retrofit] configurada con [URL_BASE] y [GsonConverterFactory].
     * Se inicializa de forma perezosa (lazy).
     */
    private val retrofit: Retrofit by lazy {
        println("🔄 Configurando Retrofit con URL: $URL_BASE")
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Instancia de la [PromocionJovenAPI] (servicio de Retrofit) creada a partir de [retrofit].
     * Se inicializa de forma perezosa (lazy).
     */
    private val servicio: PromocionJovenAPI by lazy {
        println("🔄 Creando servicio API")
        retrofit.create(PromocionJovenAPI::class.java)
    }

    /**
     * Obtiene la lista *completa* de promociones para jóvenes,
     * manejando automáticamente la paginación y los reintentos.
     *
     * Llama a [descargarTodasLasPaginas] y proporciona un try-catch global.
     *
     * @return Una [List] de [PromocionJoven]. Retorna una lista vacía si
     * la descarga completa falla por cualquier excepción.
     */
    suspend fun obtenerPromociones(): List<PromocionJoven> {
        println("🔄 Iniciando obtención de promociones")
        return try {
            val resultado = descargarTodasLasPaginas()
            println("✅ Obtención completada: ${resultado.size} promociones")
            resultado
        } catch (e: Exception) {
            println("❌ Error en obtenerPromociones: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Función privada que implementa el bucle de paginación.
     *
     * Comienza en la página 1 y continúa solicitando páginas
     * (usando [obtenerPaginaConReintentos]) mientras la respuesta
     * indique que [Pagination.has_next] es verdadero.
     *
     * Agrega los resultados de cada página a una lista mutable.
     *
     * @return La lista [List] de [PromocionJoven] acumulada de todas las páginas.
     * @throws Exception Si [obtenerPaginaConReintentos] falla definitivamente.
     */
    private suspend fun descargarTodasLasPaginas(): List<PromocionJoven> {
        println("🔄 Iniciando descarga de páginas")
        val todasLasPromociones = mutableListOf<PromocionJoven>()
        var paginaActual = 1
        var tieneMasPaginas = true

        while (tieneMasPaginas) {
            try {
                println("📄 Descargando página $paginaActual")
                val response = obtenerPaginaConReintentos(paginaActual, 50)

                println("📄 Página $paginaActual - Datos recibidos: ${response.data.size} items")
                println("📄 Paginación - has_next: ${response.pagination.has_next}")

                if (response.data.isNotEmpty()) {
                    todasLasPromociones.addAll(response.data)
                    println("📄 Total acumulado: ${todasLasPromociones.size}")
                }

                tieneMasPaginas = response.pagination.has_next
                paginaActual++

                if (tieneMasPaginas) {
                    delay(50L) // Pequeña pausa entre solicitudes exitosas
                }

            } catch (e: Exception) {
                // Manejo de error en el bucle de paginación
                println("❌ Error en página $paginaActual: ${e.message}")
                paginaActual++
                // Límite de seguridad para evitar bucles infinitos en caso de error persistente
                tieneMasPaginas = paginaActual < 10
                delay(1000L) // Pausa más larga antes de intentar la siguiente página
            }
        }

        println("✅ Descarga completada: ${todasLasPromociones.size} promociones totales")
        return todasLasPromociones
    }

    /**
     * Intenta obtener una página específica del servicio API,
     * reintentando en caso de fallo.
     *
     * Realiza hasta [reintentos] intentos. Si un intento falla,
     * espera un tiempo (delay) que aumenta con cada intento.
     *
     * @param pagina El número de página a solicitar.
     * @param limite El tamaño de la página (cantidad de ítems).
     * @param reintentos El número máximo de intentos a realizar (default: [MAX_REINTENTOS]).
     * @return El [PromocionesJovenResponse] si la solicitud es exitosa.
     * @throws Exception Lanza la última excepción capturada si todos
     * los [reintentos] fallan.
     */
    private suspend fun obtenerPaginaConReintentos(
        pagina: Int,
        limite: Int,
        reintentos: Int = MAX_REINTENTOS
    ): PromocionesJovenResponse {
        println("🔄 Obteniendo página $pagina (reintentos: $reintentos)")
        var ultimoError: Exception? = null

        repeat(reintentos) { intento ->
            try {
                println("🔄 Intento ${intento + 1} para página $pagina")
                val response = servicio.obtenerPromociones(pagina, limite)
                println("✅ Página $pagina obtenida exitosamente")
                return response // Éxito, retorna la respuesta
            } catch (e: Exception) {
                println("❌ Intento ${intento + 1} falló: ${e.message}")
                ultimoError = e
                if (intento < reintentos - 1) {
                    // Backoff simple: 1s, 2s, 3s...
                    val delayMs = 1000L * (intento + 1)
                    println("⏳ Esperando $delayMs ms antes de reintentar...")
                    delay(delayMs)
                }
            }
        }

        println("❌ Todos los reintentos fallaron para página $pagina")
        // Si todos los reintentos fallan, lanza la última excepción capturada
        throw ultimoError ?: Exception("Error desconocido al obtener página $pagina")
    }
}