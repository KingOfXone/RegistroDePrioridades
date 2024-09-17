package edu.ucne.prioridades.presentation.navigation.ticket

import edu.ucne.prioridades.data.entities.PrioridadEntity
import edu.ucne.prioridades.data.entities.TicketEntity


data class TicketUiState(
    val ticketId: Int? = null,
    val fecha: String = "",
    val prioridadId: Int? = null,  // Ensure this is correctly defined
    val cliente: String = "",
    val asunto: String = "",
    val descripcion: String = "",
    val errorFecha: String = "",
    val errorPrioridad: String = "",
    val errorCliente: String = "",
    val errorAsunto: String = "",
    val errorDescripcion: String = "",
    var validation: Boolean = false,
    val tickets: List<TicketEntity> = emptyList(),
    val prioridades: List<PrioridadEntity> = emptyList(),
    val success: Boolean = false
)