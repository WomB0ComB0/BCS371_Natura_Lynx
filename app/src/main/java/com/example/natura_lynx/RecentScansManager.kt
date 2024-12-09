package com.example.natura_lynx

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// Define the DataStore at the file level
private val Context.dataStore by preferencesDataStore("recent_scans")

class RecentScansManager(private val context: Context) {
    private val recentScansKey = stringPreferencesKey("recent_scans")

    suspend fun saveRecentScan(scan: IdentifiedPlant) {
        context.dataStore.edit { preferences ->
            val currentScans = getRecentScans().toMutableList()
            currentScans.add(0, scan)
            if (currentScans.size > 3) {
                currentScans.removeAt(3)
            }
            preferences[recentScansKey] = Json.encodeToString(currentScans)
        }
    }

    suspend fun getRecentScans(): List<IdentifiedPlant> {
        return try {
            val scansJson = context.dataStore.data.first()[recentScansKey] ?: return emptyList()
            Json.decodeFromString<List<IdentifiedPlant>>(scansJson)
        } catch (e: Exception) {
            emptyList()
        }
    }
} 