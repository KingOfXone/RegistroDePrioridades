package edu.ucne.prioridades.presentation.navigation.ticket


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.prioridades.R
import edu.ucne.prioridades.data.entities.PrioridadEntity
import edu.ucne.prioridades.data.entities.TicketEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable

fun TicketListScreen(
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: TicketViewModel = hiltViewModel(),
    onTicketClick: (Int) -> Unit,
    onAddTicket: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TicketListBodyScreen(
        drawerState = drawerState,
        scope = scope,
        uiState = uiState,
        onTicketClick = onTicketClick,
        onAddTicket = onAddTicket,
        onDeleteTicket = { ticketId ->
            viewModel.onEvent(TicketUiEvent.SelectTicket(ticketId))
            viewModel.onEvent(TicketUiEvent.Delete(ticketId))
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListBodyScreen(
    drawerState: DrawerState,
    scope: CoroutineScope,
    uiState: TicketUiState,
    onTicketClick: (Int) -> Unit,
    onAddTicket: () -> Unit,
    onDeleteTicket: (Int) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lista de Tickets",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTicket) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar nuevo ticket"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 15.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (uiState.tickets.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Lista vacía",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    items(uiState.tickets) { ticket ->
                        TicketRow(
                            ticket = ticket,
                            prioridades = uiState.prioridades,
                            onTicketClick = onTicketClick,
                            onDeleteTicket = onDeleteTicket
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketRow(
    ticket: TicketEntity,
    prioridades: List<PrioridadEntity>,
    onTicketClick: (Int) -> Unit,
    onDeleteTicket: (Int) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var showDeleteConfirmation by remember { mutableStateOf(false) }


    val formattedDate: String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(ticket.fecha)
        date?.let { dateFormat.format(it) } ?: "Fecha no disponible"
    } catch (e: Exception) {
        "Fecha no válida"
    }

    val descripcionPrioridad = prioridades.find { it.prioridadId == ticket.PrioridadId }
        ?.descripcion ?: "Sin Prioridad"

    Card(
        onClick = { onTicketClick(ticket.TicketId ?: 0) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB0BEC5)),
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .heightIn(min = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ticket),
                contentDescription = "Imagen Ticket",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Prioridad: $descripcionPrioridad",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cliente: ${ticket.Cliente}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Asunto: ${ticket.Asunto}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Descripción: ${ticket.Descripcion}",
                    style = MaterialTheme.typography.bodyLarge
                )

            }
            IconButton(
                onClick = { showDeleteConfirmation = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar este Ticket?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteTicket(ticket.TicketId ?: 0)
                            showDeleteConfirmation = false
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TicketListScreenPreview() {
    MaterialTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // Simulando datos de muestra
        val sampleTickets = listOf(
            TicketEntity(
                TicketId = 1,
                fecha = "2024-09-15",
                PrioridadId = 1,
                Cliente = "Cliente A",
                Asunto = "Asunto A",
                Descripcion = "Descripción A"
            ),
            TicketEntity(
                TicketId = 2,
                fecha = "2024-09-16",
                PrioridadId = 2,
                Cliente = "Cliente B",
                Asunto = "Asunto B",
                Descripcion = "Descripción B"
            )
        )

        val samplePrioridades = listOf(
            PrioridadEntity(prioridadId = 1, descripcion = "Alta", diasCompromiso = 5),
            PrioridadEntity(prioridadId = 2, descripcion = "Media", diasCompromiso = 8)
        )

        val sampleUiState = TicketUiState(
            tickets = sampleTickets,
            prioridades = samplePrioridades
        )


        TicketListBodyScreen(
            drawerState = drawerState,
            scope = scope,
            uiState = sampleUiState,
            onTicketClick = {},
            onAddTicket = {},
            onDeleteTicket = {}
        )
    }
}