package com.example.lab4

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStoreManager(val context: Context) {

    private val nicknameKey = stringPreferencesKey("nickname")
    private val notificationsKey = booleanPreferencesKey("notifications")
    private val themeKey = stringPreferencesKey("theme")

    val nickname: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[nicknameKey] ?: ""
        }

    val notifications: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[notificationsKey] ?: false
        }

    val theme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[themeKey] ?: "light"
        }

    suspend fun setNickname(nickname: String) {
        context.dataStore.edit {
            it[nicknameKey] = nickname
        }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit {
            it[notificationsKey] = enabled
        }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit {
            it[themeKey] = theme
        }
    }
}