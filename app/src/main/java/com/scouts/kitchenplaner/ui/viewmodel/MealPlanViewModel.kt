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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.AllergenCheck
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.usecases.CheckAllergens
import com.scouts.kitchenplaner.model.usecases.EditMealPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * View model that handles a meal plan. It handles the allergen check and adding, swapping and deleting of recipes in meal slots.
 */
@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val checkAllergens: CheckAllergens, private val editMealPlan: EditMealPlan
) : ViewModel() {
    var recipeQuery by mutableStateOf("")
        private set

    var recipeToExchange = Pair<MealSlot, RecipeStub?>(MealSlot(Date(0), ""), null)

    /**
     * Gets all recipes that names matches the recipe query but it does not include the recipes which are already in the meal slot.
     *
     * @param project The project for which the recipes should be looked for.
     * @param mealSlot The meal slot for which the recipe should be looked for.
     * @return a flow of all the possible recipes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getRecipeSuggestions(project: Project, mealSlot: MealSlot) =
        snapshotFlow { recipeQuery }.flatMapLatest {
            editMealPlan.findRecipesForQuery(project, mealSlot, it)
        }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = listOf())

    /**
     * Provides which allergens are covered or not for a meal slot.
     *
     * @param project The project in which the meal slot is.
     * @param slot The meal slot for which the allergen cover is meant for.
     * @return which allergens are covered, not covered or undecided.
     */
    fun getAllergenCheck(project: Project, slot: MealSlot): StateFlow<AllergenCheck> {
        return checkAllergens.getAllergenCheck(project, slot).stateIn(
            scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = AllergenCheck()
        )
    }

    /**
     * Swaps all recipes which are contained in two meal slots
     *
     * @param project the project which contains the meal slot
     * @param first the first meal slot which contains all the first recipes
     * @param second the second meal slot which contains all the second recipes.
     */
    fun swapMeals(project: Project, first: MealSlot, second: MealSlot) {
        viewModelScope.launch {
            editMealPlan.swapMealSlots(project, first, second)
        }
    }

    /**
     * deletes the main recipe for a meal slot.
     *
     * @param project The project where the meal slot is in
     * @param slot The meal slot from which the main recipe should be deleted
     */
    fun onDeleteMainRecipe(project: Project, slot: MealSlot) {
        viewModelScope.launch {
            editMealPlan.removeMainRecipeFromMeal(project, slot)
        }
    }

    /**
     * Deletes a given alternative recipe from a meal slot

     *
     * @param project The project to which the meal slot belongs
     * @param slot The meal slot where the alternative recipe should be deleted on
     * @param recipeStub The recipe which should be deleted.
     */
    fun onDeleteAlternativeRecipe(project: Project, slot: MealSlot, recipeStub: RecipeStub) {
        viewModelScope.launch {
            editMealPlan.removeAlternativeRecipeFromMeal(project, slot, recipeStub)
        }
    }

    /**
     * If the recipe query changes it is going to be updated here.
     *
     * @param newQuery The new recipe Query
     */
    fun onRecipeQueryChanged(newQuery: String) {
        recipeQuery = newQuery
    }

    /**
     * Exchanges a recipe by another recipe
     *
     * @param project The project where the recipes should be exchanged
     * @param mealSlot The meal slot where this recipe is in
     * @param oldRecipe The recipe that is currently in the meal slot and should be exchanged
     * @param newRecipe The recipe that is the replacement
     */
    fun exchangeRecipe(
        project: Project, mealSlot: MealSlot, oldRecipe: RecipeStub, newRecipe: RecipeStub
    ) {
        viewModelScope.launch {
            if (project.mealPlan[mealSlot].first?.first?.id == oldRecipe.id) {
                editMealPlan.removeMainRecipeFromMeal(project, mealSlot)
                editMealPlan.selectMainRecipeForMealSlot(project, mealSlot, newRecipe)
            } else {
                editMealPlan.removeAlternativeRecipeFromMeal(project, mealSlot, oldRecipe)
                editMealPlan.addAlternativeRecipeForMealSlot(project, mealSlot, newRecipe)
            }
        }
    }

    /**
     * Adds a new recipe to a meal slot. If this is the first recipe in the meal slot, The recipe is going to be the main recipe
     * @param project The project where the meal slot is in
     * @param mealSlot The meal slot where the recipe should added to
     * @param newRecipe recipe that should added
     */
    fun addRecipe(project: Project, mealSlot: MealSlot, newRecipe: RecipeStub) {
        viewModelScope.launch {
            if (project.mealPlan[mealSlot].first != null) {
                editMealPlan.addAlternativeRecipeForMealSlot(project, mealSlot, newRecipe)
            } else {
                editMealPlan.selectMainRecipeForMealSlot(project, mealSlot, newRecipe)
            }
        }
    }
}