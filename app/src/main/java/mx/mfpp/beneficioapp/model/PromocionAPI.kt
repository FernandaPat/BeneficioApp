package mx.mfpp.beneficioapp.model

import retrofit2.http.GET
import retrofit2.http.Headers

interface PromocionAPI {
    @Headers(
        "X-Master-Key: \$2a\$10\$JYQOd/U8seoZ8OSE5EgzFu.Hxq62dNEdmwT6k6EPd7d72xg1eqFta"
    )
    @GET("b/68e5c8e043b1c97be95e3bb6/latest")
    suspend fun obtenerDatos(): DatosAPI
}

