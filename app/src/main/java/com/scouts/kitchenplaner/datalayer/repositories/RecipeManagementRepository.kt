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

package com.scouts.kitchenplaner.datalayer.repositories

import com.scouts.kitchenplaner.datalayer.daos.RecipeManagementDAO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectMealIdentifier
import com.scouts.kitchenplaner.datalayer.entities.AlternativeRecipeProjectMealEntity
import com.scouts.kitchenplaner.datalayer.entities.MainRecipeProjectMealEntity
import com.scouts.kitchenplaner.model.entities.MealSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class RecipeManagementRepository @Inject constructor(
    private val recipeManagementDAO: RecipeManagementDAO
){
    /**
     * Selects the main recipe for a meal
     */
    suspend fun chooseMainRecipeForMealSlot(projectId: Long, recipeId: Long, mealSlot: MealSlot) {
        recipeManagementDAO.addMainRecipeToProjectMeal(
            MainRecipeProjectMealEntity(projectId, mealSlot.meal, mealSlot.date, recipeId)
        )
    }

    suspend fun addAlternativeRecipeForMealSlot(projectId: Long, recipeId: Long, mealSlot: MealSlot) {
        recipeManagementDAO.addSingleAlternativeRecipeToProjectMeal(
            AlternativeRecipeProjectMealEntity(projectId, mealSlot.meal, mealSlot.date, recipeId)
        )
    }

    suspend fun swapRecipes(projectId: Long, firstMealSlot: MealSlot, secondMealSlot: MealSlot) {
        recipeManagementDAO.swapMeals(
            projectId,
            firstMealSlot.meal,
            firstMealSlot.date,
            secondMealSlot.meal,
            secondMealSlot.date
        )
    }

    suspend fun removeRecipesFromMeal(projectId: Long, mealSlot: MealSlot) {
        recipeManagementDAO.removeAllRecipesFromMeal(projectId, mealSlot.meal, mealSlot.date)
    }

    suspend fun removeMainRecipeFromMealSlot(projectId: Long, mealSlot: MealSlot) {
        recipeManagementDAO.removeMainRecipeFromProjectMeal(ProjectMealIdentifier(projectId, mealSlot.meal, mealSlot.date))
    }

    suspend fun removeAlternativeRecipeFromMealSlot(projectId: Long, mealSlot: MealSlot, recipeId: Long) {
        recipeManagementDAO.removeSingleAlternativeRecipeFromMeal(
            AlternativeRecipeProjectMealEntity(projectId, mealSlot.meal, mealSlot.date, recipeId)
        )
    }

    fun getRecipeMealSlotsForProject(projectId: Long) : Flow<List<Pair<MealSlot, Pair<Long, List<Long>>>>> {
        return recipeManagementDAO.getMainRecipesForProject(projectId)
            .combine(recipeManagementDAO.getAlternativeRecipesForProject(projectId)) { main, alternatives ->
            main.map { entity ->
                Pair(MealSlot(entity.date, entity.meal), Pair(entity.recipeId, alternatives.filter {
                    it.date == entity.date && it.meal == entity.meal
                }.map { it.recipeId }))
            }
        }
    }
}