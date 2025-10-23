/**
 * Archivo: ServicioRemotoJovenesPromocion.kt
 *
 * Define un servicio remoto encargado de obtener todas las promociones disponibles
 * para los usuarios jóvenes desde el servidor, mediante llamadas HTTP GET.
 *
 * Utiliza Retrofit y un sistema de paginación automática con reintentos
 * para garantizar la descarga completa de los datos incluso en caso de fallos parciales.
 */
package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.delay
/**
 * Objeto singleton responsable de la comunicación con el servicio remoto
 * de promociones para jóvenes.
 *
 * Implementa lógica de paginación, reintentos y control de errores
 * durante la obtención de los datos desde el backend.
 */
object ServicioRemotoJovenesPromocion {
    private const val URL_BASE = "https://listar-promociones-819994103285.us-central1.run.app/"
    private const val MAX_REINTENTOS = 3

    private val retrofit: Retrofit by lazy {
        println("🔄 Configurando Retrofit con URL: $URL_BASE")
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicio: PromocionJovenAPI by lazy {
        println("🔄 Creando servicio API")
        retrofit.create(PromocionJovenAPI::class.java)
    }
    /**
     * Obtiene todas las promociones disponibles para los usuarios jóvenes.
     *
     * Internamente descarga todas las páginas disponibles utilizando el método
     * [descargarTodasLasPaginas] y gestiona los posibles errores de red.
     *
     * @return Lista de objetos [PromocionJoven] obtenidos del servidor,
     * o una lista vacía si ocurre algún error.
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
     * Descarga todas las páginas de promociones desde el servidor hasta que no existan más.
     *
     * Implementa una pausa corta entre páginas y control de errores por reintento.
     *
     * @return Lista completa de [PromocionJoven] combinando todas las páginas obtenidas.
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
                    delay(50L)
                }

            } catch (e: Exception) {
                println("❌ Error en página $paginaActual: ${e.message}")
                paginaActual++
                tieneMasPaginas = paginaActual < 10
                delay(1000L)
            }
        }

        println("✅ Descarga completada: ${todasLasPromociones.size} promociones totales")
        return todasLasPromociones
    }
    /**
     * Obtiene una página específica de promociones con reintentos automáticos en caso de error.
     *
     * Aplica una estrategia de reintentos progresivos con espera incremental.
     *
     * @param pagina Número de página a solicitar.
     * @param limite Cantidad máxima de registros por página.
     * @param reintentos Número de intentos máximos antes de fallar definitivamente.
     * @return Objeto [PromocionesJovenResponse] con los datos y la información de paginación.
     * @throws Exception Si no se logra obtener la página tras todos los reintentos.
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
                return response
            } catch (e: Exception) {
                println("❌ Intento ${intento + 1} falló: ${e.message}")
                ultimoError = e
                if (intento < reintentos - 1) {
                    val delayMs = 1000L * (intento + 1)
                    println("⏳ Esperando $delayMs ms antes de reintentar...")
                    delay(delayMs)
                }
            }
        }

        println("❌ Todos los reintentos fallaron para página $pagina")
        throw ultimoError ?: Exception("Error desconocido al obtener página $pagina")
    }
}