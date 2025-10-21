// mx.mfpp.beneficioapp.model.ServicioRemotoHistorial.kt
package mx.mfpp.beneficioapp.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicioRemotoHistorial {
    private const val URL_BASE = "https://9somwbyil5.execute-api.us-east-1.amazonaws.com/prod/"
    private const val MAX_REINTENTOS = 3

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val servicio: HistorialAPI by lazy {
        retrofit.create(HistorialAPI::class.java)
    }

    /**
     * Obtiene todo el historial de promociones usadas por un usuario
     */
    suspend fun obtenerHistorialUsuario(idUsuario: Int): List<HistorialPromocionUsuario> {
        return try {
            Log.d("HISTORIAL_SERVICIO", "📖 Obteniendo historial para usuario: $idUsuario")
            descargarTodasLasPaginasUsuario(idUsuario)
        } catch (e: Exception) {
            Log.e("HISTORIAL_SERVICIO", "❌ Error obteniendo historial: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Obtiene todo el historial de un establecimiento (opcional)
     */
    suspend fun obtenerHistorialEstablecimiento(idEstablecimiento: Int): List<HistorialPromocionUsuario> {
        return try {
            Log.d("HISTORIAL_SERVICIO", "📖 Obteniendo historial para establecimiento: $idEstablecimiento")
            descargarTodasLasPaginasEstablecimiento(idEstablecimiento)
        } catch (e: Exception) {
            Log.e("HISTORIAL_SERVICIO", "❌ Error obteniendo historial establecimiento: ${e.message}", e)
            emptyList()
        }
    }

    private suspend fun descargarTodasLasPaginasUsuario(idUsuario: Int): List<HistorialPromocionUsuario> {
        val todoElHistorial = mutableListOf<HistorialPromocionUsuario>()
        var paginaActual = 1
        var tieneMasPaginas = true

        while (tieneMasPaginas) {
            try {
                val response = servicio.obtenerHistorialUsuario(idUsuario, paginaActual, 20)

                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let { historialResponse ->
                        if (historialResponse.data.isNotEmpty()) {
                            todoElHistorial.addAll(historialResponse.data)
                            Log.d("HISTORIAL_SERVICIO", "✅ Página $paginaActual: ${historialResponse.data.size} registros")
                        }

                        // Verificar si hay más páginas
                        tieneMasPaginas = historialResponse.pagination.has_next
                        paginaActual++
                    }
                } else {
                    Log.e("HISTORIAL_SERVICIO", "❌ Error en página $paginaActual: ${response.code()} - ${response.message()}")
                    break
                }

            } catch (e: Exception) {
                Log.e("HISTORIAL_SERVICIO", "❌ Error en página $paginaActual: ${e.message}")
                break
            }
        }

        Log.d("HISTORIAL_SERVICIO", "🎉 Historial completo: ${todoElHistorial.size} registros")
        return todoElHistorial
    }

    private suspend fun descargarTodasLasPaginasEstablecimiento(idEstablecimiento: Int): List<HistorialPromocionUsuario> {
        val todoElHistorial = mutableListOf<HistorialPromocionUsuario>()
        var paginaActual = 1
        var tieneMasPaginas = true

        while (tieneMasPaginas) {
            try {
                val response = servicio.obtenerHistorialEstablecimiento(idEstablecimiento, paginaActual, 20)

                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let { historialResponse ->
                        if (historialResponse.data.isNotEmpty()) {
                            todoElHistorial.addAll(historialResponse.data)
                        }
                        tieneMasPaginas = historialResponse.pagination.has_next
                        paginaActual++
                    }
                } else {
                    break
                }

            } catch (e: Exception) {
                break
            }
        }

        return todoElHistorial
    }
}