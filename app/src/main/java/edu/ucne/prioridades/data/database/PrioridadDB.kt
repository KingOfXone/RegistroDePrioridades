package edu.ucne.prioridades.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.prioridades.data.dao.PrioridadDao
import edu.ucne.prioridades.data.entities.PrioridadEntity


    @Database(
        entities = [
            PrioridadEntity::class
        ],
        version = 1,
        exportSchema = false,
    )
    abstract class PrioridadDB: RoomDatabase() {
        abstract fun prioridadDAO(): PrioridadDao
    }
