package com.learining.AzkarApp.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "Football_Prefs")

class UsagePreferences(private val context: Context) {

    object PrefKeys {
        val userId = stringPreferencesKey("id")
        val verseIndex = intPreferencesKey("verse_index")
        val isDarkMode = androidx.datastore.preferences.core.booleanPreferencesKey("is_dark_mode")
        val country = stringPreferencesKey("country")
        val city = stringPreferencesKey("city")
    }

    // Write
    suspend fun SaveId(id: String?) {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.userId] = id ?: ""
        }
    }

    suspend fun saveVerseIndex(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.verseIndex] = index
        }
    }

    suspend fun saveDarkMode(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.isDarkMode] = isDark
        }
    }

    suspend fun saveCountry(country: String) {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.country] = country
        }
    }

    suspend fun saveCity(city: String) {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.city] = city
        }
    }

    // Read
    val userId: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[PrefKeys.userId] ?: "" }

    val verseIndex: Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[PrefKeys.verseIndex] ?: 0 }

    val isDarkMode: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[PrefKeys.isDarkMode] ?: false }

    val country: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[PrefKeys.country] ?: "Egypt" }

    val city: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[PrefKeys.city] ?: "Cairo" }
}