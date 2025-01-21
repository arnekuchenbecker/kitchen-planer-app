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

import androidx.compose.material3.ExperimentalMaterial3Api
import java.util.Date

@ExperimentalMaterial3Api
class StateProjectValidator(private val inputState: CreateProjectInputState) : ProjectValidator {
    override fun validateName() {
        inputState.nameIsError = inputState.name.isBlank()
    }

    override fun validateDates() {
        val start = inputState.dates.selectedStartDateMillis
        val end = inputState.dates.selectedEndDateMillis
        if (start != null && end != null) {
            if (Date(start).before(Date(end)) || start == end) {
                inputState.setDatesNotError()
            } else {
                inputState.setDatesError("Start-Datum muss vor dem End-Datum liegen!")
            }
        } else {
            inputState.setDatesError("Start- und End-Datum müssen ausgewählt werden!")
        }
    }

    override fun validateMeals() {
        inputState.mealsIsError = inputState.meals.isEmpty()
    }

    override fun validateAllergens() {
        var foundError = false

        for (allergenPerson in inputState.allergens) {
            val personStart = allergenPerson.arrivalDateMillis
            val personEnd = allergenPerson.departureDateMillis
            val projectStart = inputState.dates.selectedStartDateMillis ?: 0
            val projectEnd = inputState.dates.selectedEndDateMillis ?: Long.MAX_VALUE
            if (allergenPerson.name.isBlank()) {
                foundError = true
            } else if (personStart == null || personEnd == null || personStart > personEnd) {
                foundError = true
            } else if (personStart < projectStart) {
                foundError = true
            } else if (personStart > projectEnd) {
                foundError = true
            } else if (personEnd < projectStart) {
                foundError = true
            } else if (personEnd > projectEnd) {
                foundError = true
            }
        }

        inputState.allergensIsError = foundError
    }
}