// mx.mfpp.beneficioapp.model.ServicioRemotoJovenesPromocion
package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.delay

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