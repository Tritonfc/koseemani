package com.example.koseemani.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// At the top level of your kotlin file:
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val VOLUME_ENABLED = booleanPreferencesKey("enable_volume_listener")
fun getVolumeEnabled(context: Context): Flow<Boolean> =
    context.settingsDataStore.data.map { preferences ->
        // No type safety.
        preferences[VOLUME_ENABLED] ?: false
    }

suspend fun setVolumeEnabled(context: Context, volumeEnabled: Boolean) {
    context.settingsDataStore.edit { settings ->
        settings[VOLUME_ENABLED] = volumeEnabled
    }
}