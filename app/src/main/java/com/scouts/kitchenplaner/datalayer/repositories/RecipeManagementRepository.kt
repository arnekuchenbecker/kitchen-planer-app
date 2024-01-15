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
import com.scouts.kitchenplaner.datalayer.entities.AlternativeRecipeProjectMealEntity
import com.scouts.kitchenplaner.datalayer.entities.MainRecipeProjectMealEntity
import com.scouts.kitchenplaner.model.entities.MealSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeManagementRepository @Inject constructor(
    private val recipeManagementDAO: RecipeManagementDAO
){
    /**
     * Selects the main recipe for a meal
     */
    suspend fun selectMainRecipeForProject(projectId: Long, recipeId: Long, mealSlot: MealSlot) {
        recipeManagementDAO.addRecipeToProjectMeal(
            MainRecipeProjectMealEntity(projectId, mealSlot.meal, mealSlot.date, recipeId)
        )
    }

    suspend fun addAlternativeRecipeForProject(projectId: Long, recipeId: Long, mealSlot: MealSlot) {
        recipeManagementDAO.addAlternativeRecipeToProjectMeal(
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

    fun getRecipeMealSlotsForProject(projectId: Long) : Flow<List<Pair<MealSlot, Long>>> {
        return recipeManagementDAO.getMainRecipesForProject(projectId).map {
            it.map { entity ->
                Pair(MealSlot(entity.date, entity.meal), entity.recipeId)
            }
        }
    }
}