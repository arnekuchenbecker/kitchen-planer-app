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

@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val checkAllergens: CheckAllergens,
    private val editMealPlan: EditMealPlan
) : ViewModel() {
    var recipeQuery by mutableStateOf("")
        private set

    var recipeToExchange = Pair<MealSlot, RecipeStub?>(MealSlot(Date(0), ""), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getRecipeSuggestions(project: Project, mealSlot: MealSlot) = snapshotFlow { recipeQuery }.flatMapLatest {
        editMealPlan.findRecipesForQuery(project, mealSlot, it)
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = listOf())

    fun getAllergenCheck(project: Project, slot: MealSlot): StateFlow<AllergenCheck> {
        return checkAllergens.getAllergenCheck(project, slot).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AllergenCheck()
        )
    }

    fun swapMeals(project: Project, first: MealSlot, second: MealSlot) {
        viewModelScope.launch {
            editMealPlan.swapMealSlots(project, first, second)
        }
    }

    fun onDeleteMainRecipe(project: Project, slot: MealSlot) {
        viewModelScope.launch {
            editMealPlan.removeMainRecipeFromMeal(project, slot)
        }
    }

    fun onDeleteAlternativeRecipe(project: Project, slot: MealSlot, recipeStub: RecipeStub) {
        viewModelScope.launch {
            editMealPlan.removeAlternativeRecipeFromMeal(project, slot, recipeStub)
        }
    }

    fun onRecipeQueryChanged(newQuery: String) {
        recipeQuery = newQuery
    }

    fun exchangeRecipe(project: Project, mealSlot: MealSlot, oldRecipe: RecipeStub, newRecipe: RecipeStub) {
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