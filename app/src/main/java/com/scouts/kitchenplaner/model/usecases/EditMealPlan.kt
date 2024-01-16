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
import com.scouts.kitchenplaner.datalayer.repositories.RecipeManagementRepository
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.Recipe
import javax.inject.Inject

class EditMealPlan @Inject constructor(
    private val recipeManagementRepository: RecipeManagementRepository,
    private val projectRepository: ProjectRepository
) {
    suspend fun selectRecipeForMeal(project: Project, mealSlot: MealSlot, recipe: Recipe) {
        recipeManagementRepository.selectMainRecipeForProject(
            project.id,
            recipe.id ?: -1,
            mealSlot
        )
    }


    suspend fun removeRecipesFromMeal(project: Project, mealSlot: MealSlot) {
        recipeManagementRepository.removeRecipesFromMeal(project.id, mealSlot)
    }

    suspend fun swapMeals(project: Project, first: MealSlot, second: MealSlot) {
        recipeManagementRepository.swapRecipes(project.id, first, second)
    }

    suspend fun addMeal(project: Project, meal: String, index: Int = project.meals.size + 1) {
        projectRepository.addMealToProject(meal, index, project.id)
    }

    suspend fun removeMeal(project: Project, meal: String) {
        projectRepository.deleteMealFromProject(meal, project.id)
    }
}