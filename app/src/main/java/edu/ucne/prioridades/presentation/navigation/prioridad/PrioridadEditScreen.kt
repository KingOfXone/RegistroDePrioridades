package edu.ucne.prioridades.presentation.navigation.prioridad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.ucne.prioridades.data.database.PrioridadDb
import edu.ucne.prioridades.data.entities.PrioridadEntity
import kotlinx.coroutines.launch

@Composable
fun PrioridadEditScreen(
    prioridadDb: PrioridadDb,
    prioridadId: Int,
    goBack: () -> Unit
) {
    var prioridad by remember { mutableStateOf<PrioridadEntity?>(null) }
    var descripcion by remember { mutableStateOf("") }
    var diasCompromiso by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(prioridadId) {
        prioridad = prioridadDb.prioridadDAO().find(prioridadId)
        prioridad?.let {
            descripcion = it.descripcion
            diasCompromiso = it.diasCompromiso.toString()
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            Text(
                text = "Editar Prioridad",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text("Descripción") },
                value = descripcion,
                onValueChange = { descripcion = it }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text("Días Compromiso") },
                value = diasCompromiso,
                onValueChange = { newValue -> diasCompromiso = newValue },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            errorMessage?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = it,
                        color = Color.Red
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = goBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("back")
                }
                OutlinedButton(onClick = {
                    scope.launch {
                        when {
                            descripcion.isBlank() -> {
                                errorMessage = "El campo de descripción no puede estar vacío"
                            }

                            diasCompromiso.isBlank() -> {
                                errorMessage = "Todos los campos son requeridos"
                            }

                            (diasCompromiso.toIntOrNull() ?: 0) <= 0 -> {
                                errorMessage = "El campo de días compromiso debe ser mayor a 0"
                            }

                            verificarDescripcion(prioridadDb, descripcion, prioridadId) != null -> {
                                errorMessage = "La prioridad ya existe"
                            }

                            else -> {
                                prioridad?.let {
                                    val prioridadActualizada = it.copy(
                                        descripcion = descripcion,
                                        diasCompromiso = diasCompromiso.toInt()
                                    )
                                    prioridadDb.prioridadDAO().save(prioridadActualizada)
                                    descripcion = ""
                                    diasCompromiso = ""
                                    errorMessage = null
                                    goBack()
                                }
                            }
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Actualizar"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Actualizar")
                }
            }
        }
    }
}

suspend fun verificarDescripcion(
    prioridadDb: PrioridadDb,
    descripcion: String,
    prioridadId: Int
): PrioridadEntity? {
    val prioridadExistente = prioridadDb.prioridadDAO().findByDescription(descripcion)
    return if (prioridadExistente != null && prioridadExistente.prioridadId != prioridadId) {
        prioridadExistente
    } else {
        null
    }
}
