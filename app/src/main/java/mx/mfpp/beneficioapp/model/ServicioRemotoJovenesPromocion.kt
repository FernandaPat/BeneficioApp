// mx.mfpp.beneficioapp.model.ServicioRemotoJovenesPromocion
package mx.mfpp.beneficioapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.delay

object ServicioRemotoJovenesPromocion {
    private const val URL_BASE = "https://9somwbyil5.execute-api.us-east-1.amazonaws.com/prod/"
    private const val MAX_REINTENTOS = 3

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicio: PromocionJovenAPI by lazy {
        retrofit.create(PromocionJovenAPI::class.java)
    }

    suspend fun obtenerPromociones(): List<PromocionJoven> {
        return try {
            descargarTodasLasPaginas()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun descargarTodasLasPaginas(): List<PromocionJoven> {
        val todasLasPromociones = mutableListOf<PromocionJoven>()
        var paginaActual = 1
        var tieneMasPaginas = true

        while (tieneMasPaginas) {
            try {
                val response = obtenerPaginaConReintentos(paginaActual, 50)

                // Verificar si hay datos
                if (response.data.isNotEmpty()) {
                    todasLasPromociones.addAll(response.data)
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

        // Mostrar resumen de las promociones descargadas
        todasLasPromociones.take(5).forEachIndexed { index, promocion ->
            println("   ${index + 1}. ${promocion.titulo_promocion} - ${promocion.nombre_establecimiento}")
        }
        if (todasLasPromociones.size > 5) {
            println("   ... y ${todasLasPromociones.size - 5} más")
        }

        return todasLasPromociones
    }

    private suspend fun obtenerPaginaConReintentos(
        pagina: Int,
        limite: Int,
        reintentos: Int = MAX_REINTENTOS
    ): PromocionesJovenResponse {
        var ultimoError: Exception? = null

        repeat(reintentos) { intento ->
            try {
                val response = servicio.obtenerPromociones(pagina, limite)
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
    suspend fun obtenerPaginaEspecifica(pagina: Int = 1, limite: Int = 10): PromocionesJovenResponse {
        return try {
            servicio.obtenerPromociones(pagina, limite)
        } catch (e: Exception) {
            throw e
        }
    }
}