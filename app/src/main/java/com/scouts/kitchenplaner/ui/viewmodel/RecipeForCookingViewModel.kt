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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeForCooking
import com.scouts.kitchenplaner.model.usecases.DisplayRecipeForCooking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for displaying a recipe for cooking
 *
 * @param displayRecipeForCooking The domain layer access via which to load recipes
 */
@HiltViewModel
class RecipeForCookingViewModel @Inject constructor(
    private val displayRecipeForCooking: DisplayRecipeForCooking
) : ViewModel() {
    /**
     * A flow containing the latest information for cooking a recipe that has been loaded by a call
     * to [getRecipe]. If no such call has been made yet, this property is not initialized and must
     * not be used.
     */
    lateinit var recipeForCooking: StateFlow<RecipeForCooking>

    /**
     * Loads a recipe for cooking into [recipeForCooking] where it can then be observed.
     *
     * @param project The project from which to obtain calculation information
     * @param mealSlot The meal slot the recipe is cooked at
     * @param recipeID The ID of the recipe being cooked
     */
    suspend fun getRecipe(project: Project, mealSlot: MealSlot, recipeID: Long) {
        recipeForCooking = displayRecipeForCooking
            .showRecipeForCooking(project, mealSlot, recipeID)
            .stateIn(viewModelScope)
    }
}