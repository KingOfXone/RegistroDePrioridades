
package edu.ucne.prioridades.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.prioridades.data.dao.PrioridadDao
import edu.ucne.prioridades.data.entities.PrioridadEntity


@Database(
    entities = [
        PrioridadEntity::class
    ],
    version = 2,
    exportSchema = false,
)
abstract class PrioridadDb: RoomDatabase() {
    abstract fun prioridadDAO(): PrioridadDao
}
