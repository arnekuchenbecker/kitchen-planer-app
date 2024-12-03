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

import com.scouts.kitchenplaner.repositories.ProjectRepository
import com.scouts.kitchenplaner.repositories.RecipeManagementRepository
import com.scouts.kitchenplaner.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case for editing the meal plan of a project. This includes defining the main recipe and
 * alternatives for it for each meal slot within a project.
 * It also provides functionality for conveniently editing the meal plan.
 *
 * @param recipeManagementRepository Repository for managing the recipes within a project
 * @param recipeRepository Repository for receiving information about the recipes
 * @param projectRepository Repository for receiving information about the project
 */
class EditMealPlan @Inject constructor(
    private val recipeManagementRepository: RecipeManagementRepository,
    private val recipeRepository: RecipeRepository,
    private val projectRepository: ProjectRepository
) {
    /**
     * Selects a main recipe for a meal slot. There can only be one per meal slot.
     *
     * @param project Project in which the meal slot is
     * @param mealSlot Meal slot for which the recipe is selected
     * @param recipe The recipe that is going to be the main recipe
     */
    suspend fun selectMainRecipeForMealSlot(project: Project, mealSlot: MealSlot, recipe: RecipeStub) {
        recipeManagementRepository.chooseMainRecipeForMealSlot(
            project.id,
            recipe.id,
            mealSlot
        )
    }

    /**
     * Adds another alternative recipe for a main recipe withing a project
     *
     * @param project The project for which the alternative recipe is selected
     * @param mealSlot Meal slot to which the recipe is added
     * @param recipe The new alternative recipe
     */
    suspend fun addAlternativeRecipeForMealSlot(project: Project, mealSlot: MealSlot, recipe: RecipeStub) {
        recipeManagementRepository.addAlternativeRecipeForMealSlot(project.id, recipe.id, mealSlot)
    }

    /**
     * Removes a specified alternative recipe from a meal slot
     *
     * @param project Project to which the meal slot belongs
     * @param mealSlot Meal slot from which the alternative recipe should be deleted
     * @param recipe Recipe to be removed
     */
    suspend fun removeAlternativeRecipeFromMeal(project: Project, mealSlot: MealSlot, recipe: RecipeStub) {
        recipeManagementRepository.removeAlternativeRecipeFromMealSlot(
            project.id,
            mealSlot,
            recipe.id
        )
    }

    /**
     * Removes the main recipe from a meal slot
     *
     * @param project Project which contains the meal slot
     * @param mealSlot Meal slot from which the main recipe gets deleted
     */
    suspend fun removeMainRecipeFromMeal(project: Project, mealSlot: MealSlot) {
        recipeManagementRepository.removeMainRecipeFromMealSlot(project.id, mealSlot)
    }

    /**
     * Removes all recipes from a meal slot
     *
     * @param  project Project which contains the meal slot
     * @param mealSlot Meal slot from which all recipes get removes
     */
    suspend fun removeRecipesFromMealSlot(project: Project, mealSlot: MealSlot) {
        recipeManagementRepository.removeRecipesFromMeal(project.id, mealSlot)
    }

    /**
     * Swaps all recipes from the first and the second meal slot
     *
     * @param project Project that contains both meal slots
     * @param first First meal slot to swap all recipes
     * @param second Second meal slot to swap all recipes
     */
    suspend fun swapMealSlots(project: Project, first: MealSlot, second: MealSlot) {
        recipeManagementRepository.swapRecipes(project.id, first, second)
    }

    /**
     * Adds a new meal to the meal plan
     *
     * @param project Project to which a new meal is added
     * @param meal Name of the new meal
     * @param index Identifying when the meal happens w.r.t. the other meals already present
     */
    suspend fun addMeal(project: Project, meal: String, index: Int = project.meals.size) {
        projectRepository.addMealToProject(meal, index, project.id)
    }

    /**
     * Removes a meal to the meal plan.
     *
     * @param project The project from which the meal gets deleted
     * @param meal The name of the meal to be deleted
     */
    suspend fun removeMeal(project: Project, meal: String) {
        projectRepository.deleteMealFromProject(meal, project.id)
    }

    /**
     * Provides a list of recipes which title contains the query in their title but is not already
     * assigned to the meal slot.
     *
     * @param project Project in which the meal slot is
     * @param mealSlot The meal slot for which suggestions should be provided
     * @param query The query for filter the suggestions
     * @return A flow contains all recipes that match the query and might be
     * relevant for the meal slot
     */
    fun findRecipesForQuery(project: Project, mealSlot: MealSlot, query: String) : Flow<List<RecipeStub>> {
        return recipeRepository.getRecipesForQueryByName(query)
            .combine(recipeManagementRepository.getRecipesForMealSlot(project.id, mealSlot)) { suggestions, recipes ->
                suggestions.filter {
                    !recipes.any { id -> it.id == id }
                }
            }
    }
}