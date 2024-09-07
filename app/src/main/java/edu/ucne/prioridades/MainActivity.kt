package edu.ucne.prioridades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import edu.ucne.prioridades.data.database.PrioridadDb
import edu.ucne.prioridades.data.entities.PrioridadEntity
import edu.ucne.prioridades.ui.theme.PrioridadesTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var prioridadDB: PrioridadDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        prioridadDB = Room.databaseBuilder(
            applicationContext,
            PrioridadDb::class.java,
            "Prioridad.db"
        ).fallbackToDestructiveMigration()
            .build()

        setContent {
            PrioridadesTheme {
                PrioridadScreen(prioridadDb = prioridadDB)
            }
        }
    }
}

@Composable
fun PrioridadScreen(prioridadDb: PrioridadDb) {
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
            Text(
                text = "Registro de Prioridades",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan,
                    textAlign = TextAlign.Center
                )
            )

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
                }
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
                            errorMessage = "La descripción solo puede contener letras, números y espacios."
                            return@launch
                        }

                        val diasCompromisoInt = diasCompromiso.toIntOrNull()
                        if (diasCompromisoInt == null || diasCompromisoInt <= 0) {
                            errorMessage = "El número de días debe ser un número entero positivo mayor que 0."
                            return@launch
                        }


                        val prioridadExistente = prioridadDb.prioridadDAO().findByDescription(descripcion)
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

            PrioridadListScreen(prioridadList)
        }
    }
}

@Composable
fun PrioridadListScreen(prioridadList: List<PrioridadEntity>, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
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
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(prioridadList) { prioridad ->
                PrioridadRow(prioridad)
            }
        }
    }
}

@Composable
fun PrioridadRow(prioridad: PrioridadEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
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
    }
}
