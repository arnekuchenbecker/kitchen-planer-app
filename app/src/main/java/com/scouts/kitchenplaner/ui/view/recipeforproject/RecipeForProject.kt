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

package com.scouts.kitchenplaner.ui.view.recipeforproject

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.ui.view.recipes.IngredientsInput
import com.scouts.kitchenplaner.ui.viewmodel.RecipeForCookingViewModel

@Composable
fun RecipeForProjectScreen(
    project: Project,
    mealSlot: MealSlot,
    recipeID: Long,
    onNavigateToAlternative: (Long) -> Unit,
    onNavigateToRecipeDetails: (Long) -> Unit,
    recipeViewModel: RecipeForCookingViewModel = hiltViewModel()
) {
    var recipeInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = null) {
        recipeViewModel.getRecipe(project, mealSlot, recipeID)
        recipeInitialized = true
    }

    if (recipeInitialized) {
        val recipeForCooking by recipeViewModel.recipeForCooking.collectAsState()

        Column {
            Text(text = recipeForCooking.name)

            Text(text = "Anzahl Personen: ${recipeForCooking.people}")

            IngredientsInput(ingredientGroups = recipeForCooking.ingredientGroups, editable = false)

            //DisplayInstructions(recipeForCooking.instructions)

            //DisplayAlternatives(recipeForCooking.alternatives)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayIngredientGroupPreview() {
    val group1 = IngredientGroup(
        "Test",
        listOf(
            Ingredient("Mais", 1.0, "kg"),
            Ingredient("Brot", 2.0, "kg"),
            Ingredient("Salat", 3.0, "Köpfe")
        )
    )
    val group2 = IngredientGroup(
        "Zutaten",
        listOf(
            Ingredient("Spinat", 500.0, "g"),
            Ingredient("Gute Laune", 1.0, "Stk")
        )
    )
    IngredientsInput(ingredientGroups = listOf(group1, group2), editable = false)
}
