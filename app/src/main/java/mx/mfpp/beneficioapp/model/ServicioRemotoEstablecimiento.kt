// mx.mfpp.beneficioapp.model.ServicioRemotoEstablecimientos
package mx.mfpp.beneficioapp.model

import android.content.Context
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

    suspend fun obtenerEstablecimientos(context: Context? = null): List<Establecimiento> {
        return try {
            val idUsuario = context?.let { SessionManager(it).getJovenId() }
            descargarTodasLasPaginas(idUsuario)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun descargarTodasLasPaginas(idUsuario: Int?): List<Establecimiento> {
        val todosLosEstablecimientos = mutableListOf<Establecimiento>()
        var paginaActual = 1
        var tieneMasPaginas = true

        while (tieneMasPaginas) {
            try {
                val response = obtenerPaginaConReintentos(paginaActual, 50, idUsuario)

                // Verificar si hay datos
                if (response.data.isNotEmpty()) {
                    todosLosEstablecimientos.addAll(response.data)
                } else {
                }

                // Verificar si hay más páginas
                tieneMasPaginas = response.pagination.has_next

                paginaActual++

                // Pequeña pausa entre páginas
                if (tieneMasPaginas) {
                    delay(50L)
                }

            } catch (e: Exception) {
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
        idUsuario: Int? = null,
        reintentos: Int = MAX_REINTENTOS
    ): EstablecimientosResponse {
        var ultimoError: Exception? = null

        repeat(reintentos) { intento ->
            try {
                val response = servicio.obtenerEstablecimientos(pagina, limite, idUsuario)
                return response
            } catch (e: Exception) {
                ultimoError = e
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
            servicio.obtenerEstablecimientos(pagina, limite)
        } catch (e: Exception) {
            throw e
        }
    }
}