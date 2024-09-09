package edu.ucne.prioridades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import edu.ucne.prioridades.data.database.PrioridadDb
import edu.ucne.prioridades.presentation.navigation.PrioridadNavHost
import edu.ucne.prioridades.ui.theme.PrioridadesTheme

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
                val navHost = rememberNavController()
                PrioridadNavHost(prioridadDB, navHost)
            }
        }
    }
}


