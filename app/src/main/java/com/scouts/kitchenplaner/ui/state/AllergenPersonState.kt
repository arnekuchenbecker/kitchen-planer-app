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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AllergenPersonState {
    var name by mutableStateOf("")
    var arrivalDateMillis by mutableStateOf<Long?>(null)
    var arrivalMeal by mutableStateOf("")
    var departureDateMillis by mutableStateOf<Long?>(null)
    var departureMeal by mutableStateOf("")
    private val allergenList = mutableStateListOf<Pair<String, Boolean>>()
    val allergens: List<Pair<String, Boolean>>
        get() = allergenList

    fun addAllergen(allergen: String, traces: Boolean) {
        allergenList.add(Pair(allergen, traces))
    }

    fun removeAllergens(toRemove: String, tracesRemove: Boolean) {
        allergenList.removeAll { (allergen, traces) ->
            allergen == toRemove && traces == tracesRemove
        }
    }
}