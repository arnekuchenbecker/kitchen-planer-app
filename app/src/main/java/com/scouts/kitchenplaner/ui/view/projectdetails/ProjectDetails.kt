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

package com.scouts.kitchenplaner.ui.view.projectdetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.ui.viewmodel.MealPlanViewModel

/**
 * Top-Level composable for the meal plan section of the project details screen. Displays general
 * information such as what recipe is planned for which meal slot to the user.
 *
 * @param project The project for which the meal plan is displayed
 * @param onNavigateToRecipeToCook Callback function to navigate to the screen for displaying the
 *                                 specified recipe for cooking
 * @param onNavigateToRecipeCreation Callback function to navigate to the recipe creation screen
 * @param viewModel The ViewModel storing the data for this composable
 */
@Composable
fun ProjectDetails(
    project: Project,
    onNavigateToRecipeToCook: (Long, MealSlot) -> Unit,
    onNavigateToRecipeCreation: () -> Unit,
    viewModel: MealPlanViewModel = hiltViewModel()
) {
    var selectionDialogForSlot by remember { mutableStateOf<MealSlot?>(null) }

    DisplayMealPlan(
        mealSlots = project.mealSlots,
        mealPlan = project.mealPlan,
        getAllergenCheck = { slot ->
            viewModel.getAllergenCheck(project, slot)
        },
        onSwap = { first, second ->
            viewModel.swapMeals(project, first, second)
        },
        onShowRecipe = { stub, mealSlot ->
            onNavigateToRecipeToCook(stub.id, mealSlot)
        },
        onDeleteRecipe = { slot, recipe ->
            if (recipe == null) {
                viewModel.onDeleteMainRecipe(project, slot)
            } else {
                viewModel.onDeleteAlternativeRecipe(project, slot, recipe)
            }
        },
        displayRecipeSelectionDialog = { slot, exchange ->
            selectionDialogForSlot = slot
            viewModel.recipeToExchange = Pair(slot, exchange)
        }
    )

    if (selectionDialogForSlot != null) {
        val suggestions by remember {
            viewModel.getRecipeSuggestions(project, selectionDialogForSlot!!)
        }.collectAsState()
        RecipeSelectionDialog(
            onDismissRequest = { selectionDialogForSlot = null },
            onNavigateToRecipeCreation = onNavigateToRecipeCreation,
            onSelection = { newRecipe ->
                val oldRecipe = viewModel.recipeToExchange.second
                if (oldRecipe != null) {
                    viewModel.exchangeRecipe(
                        project,
                        viewModel.recipeToExchange.first,
                        oldRecipe,
                        newRecipe
                    )
                } else {
                    viewModel.addRecipe(
                        project,
                        viewModel.recipeToExchange.first,
                        newRecipe
                    )
                }
                selectionDialogForSlot = null
            },
            onQueryChange = viewModel::onRecipeQueryChanged,
            recipeQuery = viewModel.recipeQuery,
            searchResults = suggestions
        )
    }
}
