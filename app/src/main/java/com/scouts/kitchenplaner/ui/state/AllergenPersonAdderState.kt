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

package com.scouts.kitchenplaner.ui.state

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.scouts.kitchenplaner.utils.toDateString
import java.util.Locale

/**
 * State object for the AllergenPersonAdder Composable. Contains information about a single person
 * that is to be added.
 */
class AllergenPersonAdderState {
    /**
     * The name of the person
     */
    var name by mutableStateOf("")

    /**
     * Which meal is the first meal the person will be present for
     */
    var arrivalMeal by mutableStateOf("")

    /**
     * Which meal is the last meal the person will be present for
     */
    var departureMeal by mutableStateOf("")

    /**
     * DatePickerState for the date the person is arriving
     */
    @OptIn(ExperimentalMaterial3Api::class)
    var arrivalDate: DatePickerState = DatePickerState(Locale.GERMAN, null, null, IntRange(2000, 2100), DisplayMode.Picker)

    /**
     * Textual representation of the date the person is arriving
     */
    @OptIn(ExperimentalMaterial3Api::class)
    val arrivalDateString: String
        get() = (arrivalDate.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt.")

    /**
     * DatePickerState for the date the person is leaving
     */
    @OptIn(ExperimentalMaterial3Api::class)
    var departureDate: DatePickerState = DatePickerState(Locale.GERMAN, null, null, IntRange(2000, 2100), DisplayMode.Picker)

    /**
     * Textual representation of the date the person is leaving
     */
    @OptIn(ExperimentalMaterial3Api::class)
    val departureDateString: String
        get() = (departureDate.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt.")

    private val allergenCollection: SnapshotStateMap<String, Boolean> = mutableStateMapOf()

    /**
     * The allergens the person is allergic to and whether traces are an issue as well
     */
    val allergens: List<Pair<String, Boolean>>
        get() = allergenCollection.toList()

    /**
     * Adds an allergen to the person, overwriting an existing allergen if the new one is more
     * strict (i.e. traces are now an issue)
     *
     * @param allergen The allergen to be added
     * @param traces Whether traces are an issue
     */
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