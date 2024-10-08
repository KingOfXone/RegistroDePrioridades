package edu.ucne.prioridades.presentation.navigation.ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.prioridades.data.Repository.PrioridadRepository
import edu.ucne.prioridades.data.Repository.TicketRepository
import edu.ucne.prioridades.data.entities.TicketEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val prioridadRepository: PrioridadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getTickets()
        getPrioridades()
    }

    fun onEvent(event: TicketUiEvent) {
        when (event) {
            is TicketUiEvent.FechaChange -> onFechaChanged(event.fecha)
            is TicketUiEvent.PrioridadChange -> onPrioridadChanged(event.prioridadId)
            is TicketUiEvent.ClienteChange -> onClienteChanged(event.cliente)
            is TicketUiEvent.AsuntoChange -> onAsuntoChanged(event.asunto)
            is TicketUiEvent.DescripcionChange -> onDescripcionChanged(event.descripcion)
            TicketUiEvent.Save -> saveTicket()
            is TicketUiEvent.Delete -> deleteTicket(event.ticketId)
            TicketUiEvent.New -> newTicket()
            TicketUiEvent.Validation -> _uiState.value.validation = validateInput()
            is TicketUiEvent.SelectTicket -> SelectTicket(event.ticketId)
        }
    }

    private fun saveTicket() {
        viewModelScope.launch {
            ticketRepository.save(uiState.value.toEntity())
            newTicket()
        }
    }

    private fun deleteTicket(ticketId: Int) {
        viewModelScope.launch {
            ticketRepository.finId(ticketId).let { ticket ->
                ticketRepository.delete(ticket)
                newTicket()
            }
        }
    }

    private fun newTicket() {
        _uiState.update {
            it.copy(
                ticketId = null,
                fecha = "",
                prioridadId = null,
                cliente = "",
                asunto = "",
                descripcion = "",
                errorFecha = "",
                errorPrioridad = "",
                errorCliente = "",
                errorAsunto = "",
                errorDescripcion = "",
                validation = false
            )
        }
    }

    private fun getPrioridades() {
        viewModelScope.launch {
            prioridadRepository.getAll().collect { prioridades ->
                _uiState.update {
                    it.copy(prioridades = prioridades)
                }
            }
        }
    }

    private fun getTickets() {
        viewModelScope.launch {
            ticketRepository.getTicket().collect { tickets ->
                _uiState.update {
                    it.copy(tickets = tickets)
                }
            }
        }
    }

    private fun onPrioridadChanged(prioridadIdString: String) {
        val prioridadId = prioridadIdString.toIntOrNull()
        if (prioridadId != null) {
            selectedPrioridad(prioridadId)
        } else {
            _uiState.update {
                it.copy(
                    prioridadId = null,
                    errorPrioridad = "Prioridad inválida."
                )
            }
        }
    }

    private fun selectedPrioridad(prioridadId: Int) {
        viewModelScope.launch {
            val prioridad = prioridadRepository.find(prioridadId)
            _uiState.update {
                it.copy(
                    prioridadId = prioridad?.prioridadId,
                    errorPrioridad = if (prioridad == null) "Prioridad no encontrada." else ""
                )
            }
        }
    }

    private fun SelectTicket(ticketId: Int) {
        viewModelScope.launch {
            val ticket = ticketRepository.finId(ticketId)
            _uiState.update {
                it.copy(
                    ticketId = ticket.TicketId,
                    fecha = ticket.fecha,
                    prioridadId = ticket.PrioridadId,
                    cliente = ticket.Cliente,
                    asunto = ticket.Asunto,
                    descripcion = ticket.Descripcion,
                    errorFecha = "",
                    errorPrioridad = "",
                    errorCliente = "",
                    errorAsunto = "",
                    errorDescripcion = "",
                    validation = false
                )
            }
        }
    }

    private fun onFechaChanged(fecha: String) {
        _uiState.update {
            it.copy(
                fecha = fecha,
                errorFecha = if (fecha.isEmpty()) "Fecha obligatoria." else ""
            )
        }
    }

    private fun onClienteChanged(cliente: String) {
        _uiState.update {
            it.copy(
                cliente = cliente,
                errorCliente = if (_uiState.value.cliente.isBlank() || !Regex("^[a-zA-Z]+$")
                        .matches(_uiState.value.cliente)
                )
                    "Cliente obligatorio y solo debe contener letras." else ""
            )
        }
    }

    private fun onAsuntoChanged(asunto: String) {
        _uiState.update {
            it.copy(
                asunto = asunto,
                errorAsunto = if (asunto.isEmpty()) "Asunto obligatorio." else ""
            )
        }
    }

    private fun onDescripcionChanged(descripcion: String) {
        _uiState.update {
            it.copy(
                descripcion = descripcion,
                errorDescripcion = if (descripcion.isEmpty()) "Descripción obligatoria." else ""
            )
        }
    }

    private fun validateInput(): Boolean {
        var valid = true
        if (_uiState.value.fecha.isEmpty()) {
            _uiState.update { it.copy(errorFecha = "Fecha obligatoria.") }
            valid = false
        }
        if (_uiState.value.cliente.isBlank() || !Regex("^[a-zA-Z]+$").matches(_uiState.value.cliente)) {
            _uiState.update { it.copy(errorCliente = "Cliente obligatorio y solo debe contener letras.") }
            valid = false
        }
        if (_uiState.value.asunto.isEmpty()) {
            _uiState.update { it.copy(errorAsunto = "Asunto obligatorio.") }
            valid = false
        }
        if (_uiState.value.descripcion.isEmpty()) {
            _uiState.update { it.copy(errorDescripcion = "Descripción obligatoria.") }
            valid = false
        }
        if (_uiState.value.prioridadId == null) {
            _uiState.update { it.copy(errorPrioridad = "Prioridad obligatoria.") }
            valid = false
        }
        return valid
    }
}


fun TicketUiState.toEntity(): TicketEntity {
    return TicketEntity(
        TicketId = ticketId,
        fecha = fecha,
        PrioridadId = prioridadId,
        Cliente = cliente,
        Asunto = asunto,
        Descripcion = descripcion
    )
}
