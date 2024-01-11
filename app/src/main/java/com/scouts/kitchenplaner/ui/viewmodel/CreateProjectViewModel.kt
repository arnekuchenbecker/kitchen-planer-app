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
import com.scouts.kitchenplaner.model.entities.ProjectBuilder
import com.scouts.kitchenplaner.model.usecases.CreateProject
import com.scouts.kitchenplaner.ui.state.CreateProjectInputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val createProject: CreateProject
) : ViewModel() {
    var inputState by mutableStateOf(CreateProjectInputState())

    private var navigateFlow = MutableStateFlow<Long?>(null)
    val navigateTo: StateFlow<Long?>
        get() = navigateFlow

    @OptIn(ExperimentalMaterial3Api::class)
    fun onProjectCreate() {
        viewModelScope.launch {
            val startDate = inputState.startDate.selectedDateMillis?.let { Date(it) }
            val endDate = inputState.endDate.selectedDateMillis?.let { Date(it) }

            if (startDate == null || endDate == null || inputState.name == "" || inputState.meals.isEmpty()) {
                return@launch
            }

            if (inputState.allergens.any {
                    it.arrivalDateMillis == null || it.departureDateMillis == null
                            || it.departureMeal == "" || it.arrivalMeal == ""
                }) {
                return@launch
            }

            val allergenPersons = inputState.allergens.map { person ->
                AllergenPerson(
                    name = person.name,
                    allergens = person.allergens.map { (allergen, traces) ->
                        Allergen(allergen, traces)
                    },
                    arrivalDate = Date(person.arrivalDateMillis!!),
                    departureDate = Date(person.departureDateMillis!!),
                    arrivalMeal = person.arrivalMeal,
                    departureMeal = person.departureMeal
                )
            }

            val project = ProjectBuilder()
                .name(inputState.name)
                .startDate(startDate)
                .endDate(endDate)
                .meals(inputState.meals)
                .allergenPersons(allergenPersons)
                .projectImage(inputState.image ?: Uri.EMPTY)
                .build()

            val projectId = createProject.createProject(project)

            navigateFlow.emit(projectId)
        }
    }
}