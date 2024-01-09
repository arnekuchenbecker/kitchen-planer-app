/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming
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

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.scouts.kitchenplaner.toDateString
import java.util.Locale

class AllergenPersonAdderState {
    var name by mutableStateOf("")
    var arrivalMeal by mutableStateOf("")
    var departureMeal by mutableStateOf("")
    @OptIn(ExperimentalMaterial3Api::class)
    var arrivalDate: DatePickerState = DatePickerState(Locale.GERMAN, null, null, IntRange(2000, 2100), DisplayMode.Picker)
    @OptIn(ExperimentalMaterial3Api::class)
    val arrivalDateString: String
        get() = (arrivalDate.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt.")

    @OptIn(ExperimentalMaterial3Api::class)
    var departureDate: DatePickerState = DatePickerState(Locale.GERMAN, null, null, IntRange(2000, 2100), DisplayMode.Picker)
    @OptIn(ExperimentalMaterial3Api::class)
    val departureDateString: String
        get() = (departureDate.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt.")

    private val allergenCollection: SnapshotStateMap<String, Boolean> = mutableStateMapOf()
    val allergens: List<Pair<String, Boolean>>
        get() = allergenCollection.toList()

    fun addAllergen(allergen: String, traces: Boolean) {
        if(allergenCollection.containsKey(allergen)) {
            if (traces) {
                allergenCollection[allergen] = true
            }
        } else {
            allergenCollection[allergen] = traces
        }
    }
}