package mx.mfpp.beneficioapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos principal de Room para la aplicación
 * Define todas las entidades y versión de la BD
 */
@Database(
    entities = [NotificacionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Proporciona acceso al DAO de notificaciones
     */
    abstract fun notificacionDao(): NotificacionDao

    companion object {
        // Volatile asegura que el valor de INSTANCE siempre esté actualizado
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos (Singleton Pattern)
         * Thread-safe mediante synchronized
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "beneficio_app_database"
                )
                    .fallbackToDestructiveMigration() // En producción, usa migraciones
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}