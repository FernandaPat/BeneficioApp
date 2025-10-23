/**
 * Archivo: PromocionLocal.kt
 *
 * Define el modelo de datos utilizado para representar una promoción almacenada localmente
 * dentro de la aplicación, ya sea en caché, base de datos interna o para uso temporal en UI.
 *
 * Este modelo se usa comúnmente para mostrar promociones sin requerir una conexión activa
 * o como representación simplificada de una promoción obtenida del servidor.
 */
package mx.mfpp.beneficioapp.model
/**
 * Representa una promoción almacenada localmente en el dispositivo.
 *
 * Contiene los datos necesarios para mostrar una promoción en la interfaz,
 * incluyendo detalles, categoría, estado y atributos visuales.
 *
 * @property id Identificador único de la promoción
 * @property nombre Nombre o título de la promoción
 * @property descripcion Descripción breve o detallada de la promoción
 * @property descuento Valor o porcentaje del descuento (por ejemplo, "10%")
 * @property categoria Categoría o tipo de promoción (por ejemplo, "Comida", "Moda")
 * @property ubicacion Ubicación o zona del establecimiento asociado
 * @property imagenUrl URL o ruta local de la imagen asociada a la promoción
 * @property expiraEn Texto con la fecha o tiempo restante antes de la expiración (opcional)
 * @property esFavorito Indica si la promoción fue marcada como favorita por el usuario
 * @property rating Calificación promedio de la promoción (opcional)
 */
data class PromocionLocal(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val descuento: String,
    val categoria: String,
    val ubicacion: String,
    val imagenUrl: String,
    val expiraEn: String?,
    val esFavorito: Boolean,
    val rating: Double?
)
