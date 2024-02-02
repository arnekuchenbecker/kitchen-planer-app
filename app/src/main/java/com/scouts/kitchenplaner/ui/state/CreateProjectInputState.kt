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

import android.net.Uri
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.scouts.kitchenplaner.toDateString
import java.util.Locale

class CreateProjectInputState {
    var name by mutableStateOf("")
    var image by mutableStateOf<Uri?>(null)

    @OptIn(ExperimentalMaterial3Api::class)
    var startDate: DatePickerState = DatePickerState(Locale.GERMAN)
    @OptIn(ExperimentalMaterial3Api::class)
    val startDateString: String
        get() = (startDate.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt.")

    @OptIn(ExperimentalMaterial3Api::class)
    var endDate: DatePickerState = DatePickerState(Locale.GERMAN, null, null, IntRange(2000, 2100), DisplayMode.Picker)
    @OptIn(ExperimentalMaterial3Api::class)
    val endDateString: String
        get() = (endDate.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt.")

    private val mealList: SnapshotStateList<String> = mutableStateListOf()
    private val allergenList: SnapshotStateList<AllergenPersonState> = mutableStateListOf()

    private var mutableAllergenAdderState by mutableStateOf(AllergenPersonAdderState())

    val allergenAdderState: AllergenPersonAdderState
        get() = mutableAllergenAdderState

    val meals: List<String>
        get() = mealList

    val allergens: List<AllergenPersonState>
        get() = allergenList

    fun addMeal(mealName: String) {
        mealList.add(mealName)
    }

    fun removeMeal(index: Int) {
        mealList.removeAt(index)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun addIntolerantPerson() {
        if (!allergenList.any { it.name == allergenAdderState.name }) {
            val newPerson = AllergenPersonState()
            newPerson.name = allergenAdderState.name
            newPerson.arrivalDateMillis = allergenAdderState.arrivalDate.selectedDateMillis
            newPerson.arrivalMeal = allergenAdderState.arrivalMeal
            newPerson.departureDateMillis = allergenAdderState.departureDate.selectedDateMillis
            newPerson.departureMeal = allergenAdderState.departureMeal
            allergenAdderState.allergens.forEach { (allergen, traces) ->
                newPerson.addAllergen(allergen, traces)
            }
            allergenList.add(newPerson)
        }
    }

    fun resetAllergenPersonAdderState() {
        mutableAllergenAdderState = AllergenPersonAdderState()
    }

    fun removeIntolerantPerson(name: String) {
        allergenList.removeAll { it.name == name }
    }

    fun removeIntolerancy(name: String, toRemove: String, tracesRemove: Boolean) {
        val person = allergenList.find { it.name == name }

        person?.removeAllergens(toRemove, tracesRemove)

        if (person?.allergens?.isEmpty() == true) {
            allergenList.remove(person)
        }
    }
}