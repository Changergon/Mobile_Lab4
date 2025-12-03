package com.example.lab4.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lab4.characters.model.Character
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CharactersViewModel(private val repository: CharacterRepository) : ViewModel() {

    // 1. Получаем Flow из репозитория и превращаем его в StateFlow
    // Это позволит фрагменту получать обновления и переживать смены конфигурации
    val characters: StateFlow<List<Character>> = repository.characters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // 2. Логика "Холодного старта"
        viewModelScope.launch {
            // Проверяем, есть ли что-то в базе при первом запуске ViewModel
            if (repository.characters.first().isEmpty()) {
                // Если база пуста - делаем запрос к API
                refreshCharacters(11) // Номер страницы по умолчанию
            }
        }
    }

    // 3. Метод для обновления/загрузки данных
    fun refreshCharacters(page: Int) {
        viewModelScope.launch {
            repository.refreshCharacters(page, 50) // pageSize по умолчанию
        }
    }
}

// 4. Фабрика для создания ViewModel с зависимостью (репозиторием)
class CharactersViewModelFactory(private val repository: CharacterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharactersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharactersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}