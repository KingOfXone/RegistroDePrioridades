package edu.ucne.prioridades.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Prioridad")
data class PrioridadEntity(
    @PrimaryKey
    val prioridadId: Int? =null,
    val descripcion: String = "",
    val diasCompromiso: Int?
)
