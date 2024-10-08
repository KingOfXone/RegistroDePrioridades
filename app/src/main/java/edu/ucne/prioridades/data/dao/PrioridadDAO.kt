package edu.ucne.prioridades.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.prioridades.data.entities.PrioridadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrioridadDao {
    @Upsert()
    suspend fun save(prioridad: PrioridadEntity)

    @Query(
        """
            SELECT * 
            FROM Prioridades
            WHERE prioridadid =:id
            LIMIT 1
        """
    )
    suspend fun find(id: Int): PrioridadEntity?

    @Query(
        """
            SELECT *
            FROM Prioridades
            WHERE descripcion = :descripcion
            LIMIT 1
        """
    )
    suspend fun findByDescription(descripcion: String): PrioridadEntity?

    @Delete
    suspend fun delete(prioridad: PrioridadEntity)

    @Query("SELECT * FROM Prioridades")
    fun getAll(): Flow<List<PrioridadEntity>>

}