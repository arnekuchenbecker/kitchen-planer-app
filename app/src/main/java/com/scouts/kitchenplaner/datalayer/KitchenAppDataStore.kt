/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming, Anton Kadelbach, Sandra Lanz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.scouts.kitchenplaner.datalayer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.scouts.kitchenplaner.model.entities.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class KitchenAppDataStore (private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "KitchenDataStore")
        private val USERNAME = stringPreferencesKey("username")
    }

    suspend fun setCurrentUser(user: User) {
        context.dataStore.edit {
            it[USERNAME] = user.username
        }
    }

    suspend fun getCurrentUser() : User {
        return User(context.dataStore.data.map {
            it[USERNAME] ?: ""
        }.first())
    }
}