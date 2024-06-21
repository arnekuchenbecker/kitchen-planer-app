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

import com.scouts.kitchenplaner.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientList
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.Recipe
import kotlinx.coroutines.flow.first

class DisplayIngredientList (
    private val recipeRepository: RecipeRepository
) {
    suspend fun getIngredientList(project: Project) : IngredientList {
        val result = IngredientList()
        project.mealSlots.forEach { slot ->
            val mealPlanSlot = project.mealPlan[slot]
            val recipes = mealPlanSlot.first
            val numberOfPeople = mealPlanSlot.second
            if (recipes != null) {
                val recipe = recipeRepository.getRecipeById(recipes.first.id).first()
                addIngredients(result, recipe, numberOfPeople, slot)
                recipes.second.forEach {
                    val alternative = recipeRepository.getRecipeById(it.id).first()
                    addIngredients(result, alternative, 1, slot) //TODO - figure out how many people are actually eating
                }
            }
        }
        return result
    }

    @OptIn(DomainLayerRestricted::class)
    private fun addIngredients(ingredientList: IngredientList, recipe: Recipe, numberOfPeople: Int, slot: MealSlot) {
        recipe.ingredientGroups.forEach {
            it.ingredients.forEach { ingredient ->
                ingredientList.addIngredient(
                    Ingredient(
                        ingredient.name,
                        ingredient.amount * (numberOfPeople.toFloat() / recipe.numberOfPeople),
                        ingredient.unit),
                    slot
                )
            }
        }
    }
}