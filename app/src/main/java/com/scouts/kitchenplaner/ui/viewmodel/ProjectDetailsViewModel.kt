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

/**
 * The view model that contains all data and function to display a project and do edit the project.
 * @param displayProjectOverview use case that provides all data for a specified project
 * @param projectSettings use case to edit the settings for the project
 * @param editAllergens use case to add, change or delete allergens for allergen persons
 * @param editMealPlan use case to add, change or delete a recipe in a meal slot or meals
 */
@HiltViewModel
class ProjectDetailsViewModel @Inject constructor(
    private val displayProjectOverview: DisplayProjectOverview,
    private val projectSettings: EditProjectSettings,
    private val editAllergens: EditAllergens,
    private val editMealPlan: EditMealPlan
) : ViewModel() {
    lateinit var projectFlow: StateFlow<Project>

    /**
     * Provides the project to the corresponding project id
     * @param projectId id of the project (can be online id or local id)
     */
    suspend fun getProject(projectId: Long) {
        projectFlow = displayProjectOverview.getProject(projectId).stateIn(viewModelScope)
    }

    /**
     * sets the name for a given project
     * @param project the project
     * @param name the new name for the project
     */
    fun setProjectName(project: Project, name: String) {
        viewModelScope.launch {
            projectSettings.setProjectName(project, name)
        }
    }

    /**
     * sets an image for the a project by saving the uri to the image
     * @param project The project which gets a new image
     * @param uri the uri which points to the image
     */
    fun setProjectImage(project: Project, uri: Uri) {
        viewModelScope.launch {
            projectSettings.setProjectPicture(project, uri)
        }
    }

    /**
     * Sets the start and the end date of a project
     * @param project the project which gets the dates
     * @param start the date when the project starts
     * @param end the date when the project ends (inclusive)
     */
    fun setProjectDates(project: Project, start: Date, end: Date) {
        viewModelScope.launch {
            projectSettings.setProjectDates(project, start, end)
        }
    }

    /**
     * Sets whether person leave of arrive before a specified meal slot
     *
     * @param project The project where the change belongs to
     * @param changes the meal slot (first) to which a number of people (second) arrive or leave
     */
    fun setNumberChanges(project: Project, changes: Map<MealSlot, Int>) {
        viewModelScope.launch {
            projectSettings.setNumberChanges(project, changes)
        }
    }

    /**
     * Removes a allergen person from a project
     * @param project The project from which the person should be removed
     * @param person The person who is removed
     */
    fun removeAllergenPerson(project: Project, person: AllergenPerson) {
        viewModelScope.launch {
            editAllergens.removeAllergenPerson(project, person)
        }
    }

    /**
     * Removes an allergen from an allergen person
     * @param project The project where the person belongs to
     * @param person The allergen person
     * @param allergen The allergen which is going to be removed
     */
    fun removeAllergenFromPerson(project: Project, person: AllergenPerson, allergen: Allergen) {
        viewModelScope.launch {
            editAllergens.removeAllergenFromPerson(project, person, allergen)
        }
    }

    /**
     * Add a new allergen person to a project
     * @param project to which the person gets added
     * @param person new allergen person
     */
    fun addAllergenPerson(project: Project, person: AllergenPerson) {
        viewModelScope.launch {
            editAllergens.addAllergenPerson(project, person)
        }
    }

    /**
     * Adds a new meal to the project at a specified point
     * @param project to which the meal is added
     * @param meal name of the new meal
     * @param addBefore the position before which it should be added. Negative values adds the meal as first meal
     */
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

    /**
     *  Removes a meal from a project and adjust their order
     *  @param project The project from which the meal should be removed
     *  @param meal The name of the meal to remove
     */
    fun removeMeal(project: Project, meal: String) {
        viewModelScope.launch {
            editMealPlan.removeMeal(project, meal)
        }
    }
}