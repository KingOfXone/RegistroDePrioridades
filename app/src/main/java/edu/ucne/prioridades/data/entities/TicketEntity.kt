package edu.ucne.prioridades.data.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey
    val TicketId: Int? = 0,
    val Cliente: String,
    val Asunto: String,
    val fecha: String = "",
    val PrioridadId: Int?,
    val Descripcion: String
)