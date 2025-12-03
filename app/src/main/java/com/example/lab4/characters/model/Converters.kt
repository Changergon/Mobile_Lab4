package com.example.lab4.characters.model

import androidx.room.TypeConverter

class Converters {
    private val separator = ","

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(separator)
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        return data.split(separator).map { it.trim() }.filter { it.isNotEmpty() }
    }
}