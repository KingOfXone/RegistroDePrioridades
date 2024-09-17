package edu.ucne.prioridades.data.Repository

import edu.ucne.prioridades.data.dao.PrioridadDao
import edu.ucne.prioridades.data.entities.PrioridadEntity
import javax.inject.Inject

class PrioridadRepository @Inject constructor(
    private val PrioridadDao: PrioridadDao
) {
    suspend fun save(prioridad: PrioridadEntity) = PrioridadDao.save(prioridad)
    suspend fun delete(prioridad: PrioridadEntity) = PrioridadDao.delete(prioridad)
    fun getAll() = PrioridadDao.getAll()
    suspend fun find(id: Int) = PrioridadDao.find(id)

}
