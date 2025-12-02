package com.example.lab4.characters

import com.example.lab4.characters.api.RetrofitClient
import com.example.lab4.characters.model.Character

// 1. Превращаем в object, чтобы сделать его Singleton'ом
object CharacterRepository {
    private val apiService = RetrofitClient.apiService

    // 2. Добавляем поле для кэширования данных
    var cachedCharacters: List<Character> = emptyList()
        private set // Чтобы изменить список можно было только изнутри репозитория

    suspend fun getCharacters(page: Int, pageSize: Int): List<Character> {
        return try {
            val characters = apiService.getCharacters(page, pageSize)
            // 3. Сохраняем данные в кэш после успешной загрузки
            cachedCharacters = characters
            characters
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}