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

package com.scouts.kitchenplaner.ui.view.projectdetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.model.entities.AllergenCheck
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.ui.theme.KitchenPlanerTheme
import com.scouts.kitchenplaner.ui.view.PicturePicker
import com.scouts.kitchenplaner.ui.viewmodel.ProjectDetailsViewModel
import kotlinx.coroutines.flow.StateFlow

@OptIn(
    ExperimentalFoundationApi::class
)
@Composable
fun ProjectDetails(
    projectID: Long,
    onNavigateToRecipeToCook: (Long) -> Unit,
    onNavigateToRecipeCreation: () -> Unit,
    viewModel: ProjectDetailsViewModel = hiltViewModel()
) {
    var projectInitialized by remember { mutableStateOf(false) }
    var displayRecipeSelectionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = null) {
        viewModel.getProject(projectID)
        projectInitialized = true
    }

    if (projectInitialized) {
        val project by viewModel.projectFlow.collectAsState()
        val allergenChecks = remember { mutableStateMapOf<MealSlot, StateFlow<AllergenCheck>>() }

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = project.first.name,
                    fontSize = 8.em,
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .basicMarquee(
                            iterations = Int.MAX_VALUE,
                            delayMillis = 3000
                        ),
                    maxLines = 1
                )

                PicturePicker(
                    onPathSelected = {
                        viewModel.setProjectImage(project.first, it)
                    },
                    path = project.first.projectImage,
                    modifier = Modifier
                        .height(180.dp)
                        .aspectRatio(1.0f)
                )
            }

            DisplayMealPlan(
                modifier = Modifier.padding(top = 5.dp),
                mealSlots = project.first.mealSlots,
                mealPlan = project.first.mealPlan,
                getAllergenCheck = { slot ->
                    if (!allergenChecks.containsKey(slot)) {
                        allergenChecks[slot] = viewModel.getAllergenCheck(slot)
                    }
                    allergenChecks[slot]!!
                },
                onSwap = { first, second ->
                    viewModel.swapMeals(project.first, first, second)
                },
                onShowRecipe = {
                    onNavigateToRecipeToCook(it.id ?: 0)
                },
                onDeleteRecipe = { slot, recipe ->
                    if (recipe == null) {
                        viewModel.onDeleteMainRecipe(project.first, slot)
                    } else {
                        viewModel.onDeleteAlternativeRecipe(project.first, slot, recipe)
                    }
                },
                displayRecipeSelectionDialog = { slot, exchange ->
                    displayRecipeSelectionDialog = true
                    viewModel.recipeToExchange = Pair(slot, exchange)
                }
            )
        }

        if (displayRecipeSelectionDialog) {
            val suggestions by viewModel.recipeSuggestions.collectAsState()
            RecipeSelectionDialog(
                onDismissRequest = { displayRecipeSelectionDialog = false },
                onNavigateToRecipeCreation = onNavigateToRecipeCreation,
                onSelection = { newRecipe ->
                    val oldRecipe = viewModel.recipeToExchange.second
                    if (oldRecipe != null) {
                        viewModel.exchangeRecipe(project.first, viewModel.recipeToExchange.first, oldRecipe, newRecipe)
                    } else {
                        viewModel.addRecipe(project.first, viewModel.recipeToExchange.first, newRecipe)
                    }
                    displayRecipeSelectionDialog = false
                },
                onQueryChange = viewModel::onRecipeQueryChanged,
                recipeQuery = viewModel.recipeQuery,
                searchResults = suggestions
            )
        }
    } else {
        Text(text = "Waiting for the project to be loaded.")
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProjectDetailsPreview() {
    KitchenPlanerTheme(dynamicColor = false) {
        ProjectDetails(
            projectID = 1,
            onNavigateToRecipeToCook = {},
            onNavigateToRecipeCreation = {})
    }
}
