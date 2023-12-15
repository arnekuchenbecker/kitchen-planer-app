/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023  Arne Kuchenbecker, Antonia Heiming
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

package com.scouts.kitchenplaner.ui.state

import android.net.Uri
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.scouts.kitchenplaner.toDateString

class CreateProjectInputState {
    var name by mutableStateOf("")
    var image by mutableStateOf<Uri?>(null)

    @OptIn(ExperimentalMaterial3Api::class)
    var startDate: DatePickerState = DatePickerState(null, null, IntRange(2000, 2100), DisplayMode.Picker)
    @OptIn(ExperimentalMaterial3Api::class)
    val startDateString: String
        get() = (startDate.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt.")

    @OptIn(ExperimentalMaterial3Api::class)
    var endDate: DatePickerState = DatePickerState(null, null, IntRange(2000, 2100), DisplayMode.Picker)
    @OptIn(ExperimentalMaterial3Api::class)
    val endDateString: String
        get() = (endDate.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt.")

    private val mealList: SnapshotStateList<String> = mutableStateListOf()
    private val allergenList: SnapshotStateMap<String, SnapshotStateList<Pair<String, Boolean>>> = mutableStateMapOf()

    val meals: List<String>
        get() = mealList

    val allergens: Map<String, List<Pair<String, Boolean>>>
        get() = allergenList

    fun addMeal(mealName: String) {
        mealList.add(mealName)
    }

    fun removeMeal(index: Int) {
        mealList.removeAt(index)
    }

    fun addIntolerantPerson(name: String, allergen: String, traces: Boolean) {
        if (allergenList.containsKey(name)) {
            allergenList[name]?.add(Pair(allergen, traces))
        } else {
            allergenList[name] = mutableStateListOf(Pair(allergen, traces))
        }
    }

    fun removeIntolerantPerson(name: String) {
        allergenList.remove(name)
    }

    fun removeIntolerancy(name: String, toRemove: String, tracesRemove: Boolean) {
        allergenList[name]?.removeAll {(allergen, traces) ->
            allergen == toRemove && traces == tracesRemove
        }

        if (allergenList[name]?.isEmpty() == true) {
            allergenList.remove(name)
        }
    }
}