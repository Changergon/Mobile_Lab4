package com.example.lab4.characters

import com.example.lab4.characters.api.RetrofitClient
import com.example.lab4.characters.model.Character
import com.example.lab4.characters.model.CharacterDao
import kotlinx.coroutines.flow.Flow

// 1. Превращаем обратно в class и передаем DAO
class CharacterRepository(private val characterDao: CharacterDao) {

    // 2. Предоставляем Flow данных прямо из базы данных (DAO)
    // Это и есть "наблюдаемый запрос" из пункта C.
    val characters: Flow<List<Character>> = characterDao.getAll()

    // 3. Метод для обновления данных из сети
    suspend fun refreshCharacters(page: Int, pageSize: Int) {
        try {
            // Загружаем свежие данные из API
            val freshCharacters = RetrofitClient.apiService.getCharacters(page, pageSize)
            // Очищаем старые данные в базе
            characterDao.deleteAll()
            // Вставляем новые данные
            characterDao.insertAll(freshCharacters)
        } catch (e: Exception) {
            e.printStackTrace()
            // В случае ошибки сети, данные в базе останутся нетронутыми
        }
    }
}