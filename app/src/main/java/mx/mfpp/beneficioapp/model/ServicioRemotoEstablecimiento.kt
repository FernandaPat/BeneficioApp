// mx.mfpp.beneficioapp.model.ServicioRemotoEstablecimientos
package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.delay

object ServicioRemotoEstablecimiento {
    private const val URL_BASE = "https://9somwbyil5.execute-api.us-east-1.amazonaws.com/prod/"
    private const val MAX_REINTENTOS = 3

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicio: EstablecimientoAPI by lazy {
        retrofit.create(EstablecimientoAPI::class.java)
    }

    suspend fun obtenerEstablecimientos(): List<Establecimiento> {
        return try {
            println("🟡 Iniciando descarga de todos los establecimientos...")
            descargarTodasLasPaginas()
        } catch (e: Exception) {
            println("🔴 Error fatal al descargar establecimientos: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun descargarTodasLasPaginas(): List<Establecimiento> {
        val todosLosEstablecimientos = mutableListOf<Establecimiento>()
        var paginaActual = 1
        var tieneMasPaginas = true

        while (tieneMasPaginas) {
            try {
                println("📄 Descargando página $paginaActual de establecimientos...")
                val response = obtenerPaginaConReintentos(paginaActual, 50)

                // Verificar si hay datos
                if (response.data.isNotEmpty()) {
                    todosLosEstablecimientos.addAll(response.data)
                    println("✅ Página $paginaActual: ${response.data.size} establecimientos (Total: ${todosLosEstablecimientos.size})")
                } else {
                    println("⚠️ Página $paginaActual: Sin datos")
                }

                // Verificar si hay más páginas
                tieneMasPaginas = response.pagination.has_next
                println("🔍 ¿Hay más páginas? $tieneMasPaginas")

                paginaActual++

                // Pequeña pausa entre páginas
                if (tieneMasPaginas) {
                    delay(50L)
                }

            } catch (e: Exception) {
                println("❌ Error en página $paginaActual: ${e.message}")
                // Si falla una página, continuamos con la siguiente en lugar de detener todo
                paginaActual++
                tieneMasPaginas = paginaActual < 10 // Límite de seguridad
                delay(1000L)
            }
        }

        println("🎉 Descarga completada: ${todosLosEstablecimientos.size} establecimientos en total")

        // Mostrar resumen de los establecimientos descargados
        todosLosEstablecimientos.take(5).forEachIndexed { index, establecimiento ->
            println("   ${index + 1}. ${establecimiento.nombre} - ${establecimiento.nombre_categoria}")
        }
        if (todosLosEstablecimientos.size > 5) {
            println("   ... y ${todosLosEstablecimientos.size - 5} más")
        }

        return todosLosEstablecimientos
    }

    private suspend fun obtenerPaginaConReintentos(
        pagina: Int,
        limite: Int,
        reintentos: Int = MAX_REINTENTOS
    ): EstablecimientosResponse {
        var ultimoError: Exception? = null

        repeat(reintentos) { intento ->
            try {
                println("🔗 Llamando API: página=$pagina, límite=$limite")
                val response = servicio.obtenerEstablecimientos(pagina, limite)
                println("🔗 Llamada exitosa")
                return response
            } catch (e: Exception) {
                ultimoError = e
                println("🔄 Reintento ${intento + 1}/$reintentos falló: ${e.message}")
                if (intento < reintentos - 1) {
                    delay(1000L * (intento + 1))
                }
            }
        }

        throw ultimoError ?: Exception("Error desconocido al obtener página $pagina")
    }

    // Método para probar solo una página específica
    suspend fun obtenerPaginaEspecifica(pagina: Int = 1, limite: Int = 10): EstablecimientosResponse {
        return try {
            println("🧪 Probando página $pagina de establecimientos...")
            servicio.obtenerEstablecimientos(pagina, limite)
        } catch (e: Exception) {
            println("🧪 Error en página $pagina: ${e.message}")
            throw e
        }
    }
}