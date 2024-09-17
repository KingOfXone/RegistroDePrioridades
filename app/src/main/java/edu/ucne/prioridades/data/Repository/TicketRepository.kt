package edu.ucne.prioridades.data.Repository

import edu.ucne.prioridades.data.dao.TicketDao
import edu.ucne.prioridades.data.entities.TicketEntity
import javax.inject.Inject

class TicketRepository @Inject constructor(
    private val ticketDao: TicketDao
) {
    suspend fun save(ticket: TicketEntity) = ticketDao.save(ticket)
    suspend fun delete(ticket: TicketEntity) = ticketDao.delete(ticket)
    suspend fun finId(ticketId: Int) = ticketDao.find(ticketId)
    fun getTicket() = ticketDao.getAll()

}