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

import android.net.Uri
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.Locale

/**
 * State object for the CreateProject Composable. Contains information about a project that is being
 * created
 */
class CreateProjectInputState {
    /**
     * The name of the project
     */
    var name by mutableStateOf("")

    /**
     * False if [name] contains a valid project name
     */
    var nameIsError by mutableStateOf(false)

    /**
     * The URI of the project's image
     */
    var image by mutableStateOf<Uri?>(null)

    /**
     * The date range picker's state for picking start and end date
     */
    @OptIn(ExperimentalMaterial3Api::class)
    var dates: DateRangePickerState = DateRangePickerState(Locale.GERMAN)

    /**
     * False if [dates] contains a valid date range for the project
     */
    val datesIsError: Boolean
        get() = _datesIsError
    private var _datesIsError by mutableStateOf(false)

    /**
     * Error message detailing the reason for the date if [datesIsError] is true, otherwise null
     */
    val datesErrorMessage: String?
        get() = _datesErrorMessage
    private var _datesErrorMessage by mutableStateOf<String?>(null)

    private val mealList: SnapshotStateList<String> = mutableStateListOf()
    private val allergenList: SnapshotStateList<AllergenPersonState> = mutableStateListOf()

    private var mutableAllergenAdderState by mutableStateOf(AllergenPersonAdderState())

    /**
     * State object for adding allergen persons
     */
    val allergenAdderState: AllergenPersonAdderState
        get() = mutableAllergenAdderState

    /**
     * List of all meals added to the project so far
     */
    val meals: List<String>
        get() = mealList

    /**
     * False if [meals] contains a valid list of meals
     */
    var mealsIsError by mutableStateOf(false)

    /**
     * List of all allergens persons added to the project so far
     */
    val allergens: List<AllergenPersonState>
        get() = allergenList

    /**
     * False if [allergens] contains a valid list of meals
     */
    var allergensIsError by mutableStateOf(false)

    /**
     * Adds the given meal to the project
     *
     * @param mealName The name of the meal
     */
    fun addMeal(mealName: String) {
        mealList.add(mealName)
    }

    /**
     * Removes the meal at the given index from the project
     *
     * @param index The index from which to remove the meal
     */
    fun removeMeal(index: Int) {
        mealList.removeAt(index)
    }

    /**
     * Adds the allergen person configured in [allergenAdderState] to [allergens]
     */
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

    /**
     * Resets the state of [allergenAdderState]
     */
    fun resetAllergenPersonAdderState() {
        mutableAllergenAdderState = AllergenPersonAdderState()
    }

    /**
     * Removes all allergen persons with the given name from the project
     *
     * @param name The name of the person to be removed
     */
    fun removeIntolerantPerson(name: String) {
        allergenList.removeAll { it.name == name }
    }

    /**
     * Removes an allergen from an allergen person
     *
     * @param name The name of the allergen person from which the allergen should be removed
     * @param toRemove The name of the allergen that should be removed
     * @param tracesRemove Whether traces are an issue for the allergen that is being removed
     */
    fun removeIntolerancy(name: String, toRemove: String, tracesRemove: Boolean) {
        val person = allergenList.find { it.name == name }

        person?.removeAllergens(toRemove, tracesRemove)

        if (person?.allergens?.isEmpty() == true) {
            allergenList.remove(person)
        }
    }

    fun setDatesError(reason: String) {
        _datesIsError = true
        _datesErrorMessage = reason
    }

    fun setDatesNotError() {
        _datesIsError = false
        _datesErrorMessage = null
    }
}