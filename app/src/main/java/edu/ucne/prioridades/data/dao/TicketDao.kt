package edu.ucne.prioridades.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.prioridades.data.entities.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Upsert
    suspend fun save(ticket: TicketEntity)

    @Delete
    suspend fun delete(ticket: TicketEntity)

    @Query(
        """
        SELECT * FROM TICKETS
        WHERE ticketId = :id
        LIMIT 1
    """
    )
    suspend fun find(id: Int): TicketEntity

    @Query(
        """
        SELECT * FROM TICKETS
        WHERE cliente = :cliente
        LIMIT 1
    """
    )
    suspend fun findCliente(cliente: String): TicketEntity?

    @Query(
        """
        SELECT * FROM TICKETS
        WHERE asunto = :asunto
        LIMIT 1
    """
    )
    suspend fun findAsunto(asunto: String): TicketEntity?

    @Query(
        """
        SELECT * FROM TICKETS
        WHERE descripcion = :descripcion
        LIMIT 1
    """
    )
    suspend fun findDescripcion(descripcion: String): TicketEntity?


    @Query("SELECT * FROM TICKETS")
    fun getAll(): Flow<List<TicketEntity>>
}