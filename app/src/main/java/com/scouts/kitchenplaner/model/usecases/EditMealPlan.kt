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

package com.scouts.kitchenplaner.model.usecases

import com.scouts.kitchenplaner.datalayer.repositories.ProjectRepository
import com.scouts.kitchenplaner.datalayer.repositories.RecipeManagementRepository
import com.scouts.kitchenplaner.datalayer.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class EditMealPlan @Inject constructor(
    private val recipeManagementRepository: RecipeManagementRepository,
    private val recipeRepository: RecipeRepository,
    private val projectRepository: ProjectRepository
) {
    suspend fun selectMainRecipeForMealSlot(project: Project, mealSlot: MealSlot, recipe: RecipeStub) {
        recipeManagementRepository.chooseMainRecipeForMealSlot(
            project.id,
            recipe.id ?: return,
            mealSlot
        )
    }

    suspend fun addAlternativeRecipeForMealSlot(project: Project, mealSlot: MealSlot, recipe: RecipeStub) {
        recipeManagementRepository.addAlternativeRecipeForMealSlot(project.id, recipe.id ?: return, mealSlot)
    }

    suspend fun removeAlternativeRecipeFromMeal(project: Project, mealSlot: MealSlot, recipe: RecipeStub) {
        recipeManagementRepository.removeAlternativeRecipeFromMealSlot(project.id, mealSlot, recipe.id ?: 0)
    }

    suspend fun removeMainRecipeFromMeal(project: Project, mealSlot: MealSlot) {
        recipeManagementRepository.removeMainRecipeFromMealSlot(project.id, mealSlot)
    }


    suspend fun removeRecipesFromMealSlot(project: Project, mealSlot: MealSlot) {
        recipeManagementRepository.removeRecipesFromMeal(project.id, mealSlot)
    }

    suspend fun swapMealSlots(project: Project, first: MealSlot, second: MealSlot) {
        recipeManagementRepository.swapRecipes(project.id, first, second)
    }

    suspend fun addMeal(project: Project, meal: String, index: Int = project.meals.size) {
        projectRepository.addMealToProject(meal, index, project.id)
    }

    suspend fun removeMeal(project: Project, meal: String) {
        projectRepository.deleteMealFromProject(meal, project.id)
    }

    fun findRecipesForQuery(project: Project, mealSlot: MealSlot, query: String) : Flow<List<RecipeStub>> {
        return recipeRepository.getRecipesForQueryByName(query)
            .combine(recipeManagementRepository.getRecipesForMealSlot(project.id, mealSlot)) { suggestions, recipes ->
                suggestions.filter {
                    !recipes.any { id -> it.id == id }
                }
            }
    }
}