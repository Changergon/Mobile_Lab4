package com.example.lab4.characters

import com.example.lab4.characters.api.RetrofitClient
import com.example.lab4.characters.model.Character

class CharacterRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getCharacters(page: Int, pageSize: Int): List<Character> {
        return try {
            apiService.getCharacters(page, pageSize)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}