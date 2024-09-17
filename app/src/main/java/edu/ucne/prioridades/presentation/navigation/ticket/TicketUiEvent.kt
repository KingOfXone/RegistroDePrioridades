package edu.ucne.prioridades.presentation.navigation.ticket

sealed interface TicketUiEvent {
    data class FechaChange(val fecha: String) : TicketUiEvent
    data class PrioridadChange(val prioridadId: String) : TicketUiEvent
    data class ClienteChange(val cliente: String) : TicketUiEvent
    data class AsuntoChange(val asunto: String) : TicketUiEvent
    data class DescripcionChange(val descripcion: String) : TicketUiEvent
    data class SelectTicket(val ticketId: Int) : TicketUiEvent
    data object Save : TicketUiEvent
    data class Delete(val ticketId: Int) : TicketUiEvent

    data object New : TicketUiEvent
    data object Validation : TicketUiEvent
}