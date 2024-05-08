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

@HiltViewModel
class RecipeForCookingViewModel @Inject constructor(
    private val displayRecipeForCooking: DisplayRecipeForCooking
) : ViewModel() {
    lateinit var recipeForCooking: StateFlow<RecipeForCooking>

    suspend fun getRecipe(project: Project, mealSlot: MealSlot, recipeID: Long) {
        recipeForCooking = displayRecipeForCooking
            .showRecipeForCooking(project, mealSlot, recipeID)
            .stateIn(viewModelScope)
    }
}