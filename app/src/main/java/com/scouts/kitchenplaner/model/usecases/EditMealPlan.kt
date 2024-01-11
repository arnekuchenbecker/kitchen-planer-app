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

package com.scouts.kitchenplaner.model.usecases

import com.scouts.kitchenplaner.datalayer.repositories.ProjectRepository
import com.scouts.kitchenplaner.model.entities.Project
import javax.inject.Inject

class EditMealPlan @Inject constructor(
    //TODO recipeManagementRepo - missing DataLayer support
    private val projectRepository: ProjectRepository
) {
    //TODO selectRecipeForMeal(projectId, date, meal)
    //TODO removeRecipeFromMeal(projectId, date, meal)
    //TODO swapMeals(projectId, firstMealSlot, secondMealSlot)

    suspend fun addMeal(project: Project, meal: String, index: Int = project.meals.size + 1) {
        projectRepository.addMealToProject(meal, index, project.id ?: -1)
    }

    suspend fun removeMeal(project: Project, meal: String) {
        projectRepository.deleteMealFromProject(meal, project.id ?: -1)
    }
}