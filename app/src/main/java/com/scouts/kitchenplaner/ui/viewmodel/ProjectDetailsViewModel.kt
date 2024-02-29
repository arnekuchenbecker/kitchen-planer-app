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

package com.scouts.kitchenplaner.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.usecases.DisplayProjectOverview
import com.scouts.kitchenplaner.model.usecases.EditAllergens
import com.scouts.kitchenplaner.model.usecases.EditMealPlan
import com.scouts.kitchenplaner.model.usecases.EditProjectSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProjectDetailsViewModel @Inject constructor(
    private val displayProjectOverview: DisplayProjectOverview,
    private val projectSettings: EditProjectSettings,
    private val editAllergens: EditAllergens,
    private val editMealPlan: EditMealPlan
) : ViewModel() {
    lateinit var projectFlow: StateFlow<Project>
    suspend fun getProject(projectId: Long) {
        projectFlow = displayProjectOverview
            .getProject(projectId)
            .stateIn(viewModelScope)
    }

    fun setProjectName(project: Project, name: String) {
        viewModelScope.launch {
            projectSettings.setProjectName(project, name)
        }
    }

    fun setProjectImage(project: Project, uri: Uri) {
        viewModelScope.launch {
            projectSettings.setProjectPicture(project, uri)
        }
    }

    fun setProjectDates(project: Project, start: Date, end: Date) {
        viewModelScope.launch {
            projectSettings.setProjectDates(project, start, end)
        }
    }

    fun setNumberChanges(project: Project, changes: Map<MealSlot, Int>) {
        viewModelScope.launch {
            projectSettings.setNumberChanges(project, changes)
        }
    }

    fun removeAllergenPerson(project: Project, person: AllergenPerson) {
        viewModelScope.launch {
            editAllergens.removeAllergenPerson(project, person)
        }
    }

    fun removeAllergenFromPerson(project: Project, person: AllergenPerson, allergen: Allergen) {
        viewModelScope.launch {
            editAllergens.removeAllergenFromPerson(project, person, allergen)
        }
    }

    fun addAllergenPerson(project: Project, person: AllergenPerson) {
        viewModelScope.launch {
            editAllergens.addAllergenPerson(project, person)
        }
    }

    fun addMeal(project: Project, meal: String, addBefore: Int) {
        if (!project.meals.contains(meal)) {
            viewModelScope.launch {
                if (addBefore < 0) {
                    editMealPlan.addMeal(project, meal)
                } else {
                    editMealPlan.addMeal(project, meal, addBefore)
                }
            }
        }
    }

    fun removeMeal(project: Project, meal: String) {
        viewModelScope.launch {
            editMealPlan.removeMeal(project, meal)
        }
    }
}