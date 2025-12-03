package com.example.lab4.characters.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "characters")
data class Character(
    @PrimaryKey
    val name: String,
    val culture: String = "N/A",
    val born: String = "N/A",
    val titles: List<String> = emptyList(),
    val aliases: List<String> = emptyList(),
    val playedBy: List<String> = emptyList()
)
