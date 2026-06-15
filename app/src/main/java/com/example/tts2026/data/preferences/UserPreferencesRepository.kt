package com.example.tts2026.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPreferencesDataStore by preferencesDataStore(
    name = "user_preferences"
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val sessionEmail: Flow<String?> = context.userPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[SESSION_EMAIL] }

    val selectedHomeTab: Flow<Int> = context.userPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[SELECTED_HOME_TAB] ?: USERS_TAB }

    suspend fun saveSession(email: String) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[SESSION_EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.userPreferencesDataStore.edit { preferences ->
            preferences.remove(SESSION_EMAIL)
        }
    }

    suspend fun saveSelectedHomeTab(tab: Int) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[SELECTED_HOME_TAB] = tab
        }
    }

    private companion object {
        val SESSION_EMAIL = stringPreferencesKey("session_email")
        val SELECTED_HOME_TAB = intPreferencesKey("selected_home_tab")
        const val USERS_TAB = 0
    }
}
