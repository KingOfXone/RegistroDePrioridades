package edu.ucne.prioridades.presentation.navigation.prioridad

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.prioridades.data.database.PrioridadDb
import edu.ucne.prioridades.data.entities.PrioridadEntity
import kotlinx.coroutines.launch

@Composable
fun PrioridadScreen(prioridadDb: PrioridadDb, goBack: () -> Unit) {
    var descripcion by remember { mutableStateOf("") }
    var diasCompromiso by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val prioridadList by prioridadDb.prioridadDAO().getAll()
        .collectAsStateWithLifecycle(
            initialValue = emptyList(),
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.STARTED
        )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = goBack
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    Text(text = "Atrás")
                }

                Text(
                    text = "Registro de Prioridades",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green,
                        textAlign = TextAlign.Center
                    )
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Descripción") },
                value = descripcion,
                textStyle = TextStyle(textAlign = TextAlign.Center),
                onValueChange = {
                    descripcion = it
                    errorMessage = ""
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Días compromiso") },
                value = diasCompromiso,
                textStyle = TextStyle(textAlign = TextAlign.Center),
                onValueChange = {
                    diasCompromiso = it
                    errorMessage = ""
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        if (descripcion.isBlank() || diasCompromiso.isBlank()) {
                            errorMessage = "Los campos son obligatorios."
                            return@launch
                        }

                        if (!descripcion.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                            errorMessage =
                                "La descripción solo puede contener letras, números y espacios."
                            return@launch
                        }

                        val diasCompromisoInt = diasCompromiso.toIntOrNull()
                        if (diasCompromisoInt == null || diasCompromisoInt <= 0) {
                            errorMessage =
                                "El número de días debe ser un número entero positivo mayor que 0."
                            return@launch
                        }


                        val prioridadExistente =
                            prioridadDb.prioridadDAO().findByDescription(descripcion)
                        if (prioridadExistente != null) {
                            errorMessage = "Ya existe una prioridad con esta descripción."
                            return@launch
                        }


                        val nuevaPrioridad = PrioridadEntity(
                            descripcion = descripcion,
                            diasCompromiso = diasCompromisoInt
                        )
                        prioridadDb.prioridadDAO().save(nuevaPrioridad)
                        descripcion = ""
                        diasCompromiso = ""
                        errorMessage = ""
                    }
                }
            ) {
                Text(text = "Guardar")
                Icon(Icons.Default.Add, contentDescription = "Guardar")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}