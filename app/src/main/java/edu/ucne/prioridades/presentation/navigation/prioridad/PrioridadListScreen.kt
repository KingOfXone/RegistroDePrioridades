package edu.ucne.prioridades.presentation.navigation.prioridad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.ucne.prioridades.data.entities.PrioridadEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrioridadListScreen(
    prioridadList: List<PrioridadEntity>,
    createPrioridad: () -> Unit,
    onEditPrioridad: (PrioridadEntity) -> Unit,
    onDeletePrioridad: (PrioridadEntity) -> Unit // Nueva función para eliminar
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prioridades") }
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .padding(innerPadding)

        ) {
            Button(onClick = createPrioridad,) {
                Text(text = "Create Prioridad")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ID",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Descripción",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(3f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Días",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Acciones",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(prioridadList) { prioridad ->
                    PrioridadRow(
                        prioridad = prioridad,
                        onEditPrioridad = onEditPrioridad,
                        onDeletePrioridad = onDeletePrioridad
                    )
                }
            }
        }
    }
}


@Composable
fun PrioridadRow(
    prioridad: PrioridadEntity,
    onEditPrioridad: (PrioridadEntity) -> Unit,
    onDeletePrioridad: (PrioridadEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onEditPrioridad(prioridad) }, // Hacer la fila clickeable para editar
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prioridad.prioridadId.toString(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = prioridad.descripcion,
            modifier = Modifier.weight(3f),
            textAlign = TextAlign.Center
        )
        Text(
            text = prioridad.diasCompromiso.toString(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Row(Modifier.weight(1f)) {
            IconButton(onClick = { onDeletePrioridad(prioridad) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
            IconButton(onClick = { onEditPrioridad(prioridad) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}

@Preview
@Composable
private fun preview() {

    val prioridadList = listOf(
        PrioridadEntity(1, "Prioridad 1", 1),
        PrioridadEntity(2, "Prioridad 2", 2),
        PrioridadEntity(3, "Prioridad 3", 3)
    )

    PrioridadListScreen(
        prioridadList = prioridadList,
        createPrioridad = {},
        onEditPrioridad = {},
        onDeletePrioridad = {}
    )
}



