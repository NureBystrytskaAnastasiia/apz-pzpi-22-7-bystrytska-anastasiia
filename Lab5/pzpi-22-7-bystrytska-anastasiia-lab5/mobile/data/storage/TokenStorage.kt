package com.example.mobile.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class TokenStorage(private val context: Context) {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val ROLE_KEY = stringPreferencesKey("user_role")

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[TOKEN_KEY] }

    val roleFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[ROLE_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun saveRole(role: String) {
        println("Збереження ролі: $role") // Лог для відладки
        context.dataStore.edit { preferences ->
            preferences[ROLE_KEY] = role
        }
    }

    suspend fun clearRole() {
        println("Очищення ролі") // Лог для відладки
        context.dataStore.edit { preferences ->
            preferences.remove(ROLE_KEY)
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }


}
