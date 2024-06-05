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

/**
 * State object representing an allergen person
 */
class AllergenPersonState {
    /**
     * The name of the person
     */
    var name by mutableStateOf("")

    /**
     * The date the person is arriving at in milliseconds since 01 Jan 1970
     */
    var arrivalDateMillis by mutableStateOf<Long?>(null)

    /**
     * The first meal the person is present for
     */
    var arrivalMeal by mutableStateOf("")

    /**
     * The date the person is leaving at in milliseconds since 01 Jan 1970
     */
    var departureDateMillis by mutableStateOf<Long?>(null)

    /**
     * The last meal the person is present for
     */
    var departureMeal by mutableStateOf("")
    private val allergenList = mutableStateListOf<Pair<String, Boolean>>()

    /**
     * The allergens the person is allergic to and whether traces are an issue as well
     */
    val allergens: List<Pair<String, Boolean>>
        get() = allergenList

    /**
     * Adds an allergen to the person, overwriting any existing one
     *
     * @param allergen The name of the allergen
     * @param traces Whether traces are an issue
     */
    fun addAllergen(allergen: String, traces: Boolean) {
        allergenList.add(Pair(allergen, traces))
    }

    /**
     * Removes all allergens with the given name and traces
     *
     * @param toRemove The name of the allergen that should be removed
     * @param tracesRemove Whether traces are an issue for the allergen that should be removed
     */
    fun removeAllergens(toRemove: String, tracesRemove: Boolean) {
        allergenList.removeAll { (allergen, traces) ->
            allergen == toRemove && traces == tracesRemove
        }
    }
}