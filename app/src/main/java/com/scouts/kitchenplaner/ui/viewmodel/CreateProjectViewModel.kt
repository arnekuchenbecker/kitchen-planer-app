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

package com.scouts.kitchenplaner.ui.viewmodel

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.usecases.CreateProject
import com.scouts.kitchenplaner.ui.state.CreateProjectInputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.util.Date

@HiltViewModel
class CreateProjectViewModel(
    private val createProject: CreateProject
) : ViewModel() {
    var inputState by mutableStateOf(CreateProjectInputState())

    @OptIn(ExperimentalMaterial3Api::class)
    fun onProjectCreate() : Deferred<Long?> {
        return viewModelScope.async {
            val startDate = inputState.startDate.selectedDateMillis?.let { Date(it) }
            val endDate = inputState.endDate.selectedDateMillis?.let { Date(it) }

            if (startDate == null || endDate == null || inputState.name == "" || inputState.meals.isNotEmpty()) {
                return@async null
            }

            if (inputState.allergens.any {
                    it.arrivalDateMillis == null || it.departureDateMillis == null
                            || it.departureMeal == "" || it.arrivalMeal == ""
                }) {
                return@async null
            }

            val project = Project(
                name = inputState.name,
                startDate = startDate,
                endDate = endDate,
                meals = mutableListOf<String>().apply {
                    addAll(inputState.meals)
                },
                allergenPersons = mutableListOf<AllergenPerson>().apply {
                    addAll(inputState.allergens.map { person ->
                        AllergenPerson(
                            name = person.name,
                            allergens = person.allergenList.map { (allergen, traces) ->
                                Allergen(allergen, traces)
                            },
                            arrivalDate = Date(person.arrivalDateMillis!!),
                            departureDate = Date(person.departureDateMillis!!),
                            arrivalMeal = person.arrivalMeal,
                            departureMeal = person.departureMeal
                        )
                    })
                },
                projectImage = inputState.image ?: Uri.EMPTY
            )

            createProject.createProject(project)
        }
    }
}