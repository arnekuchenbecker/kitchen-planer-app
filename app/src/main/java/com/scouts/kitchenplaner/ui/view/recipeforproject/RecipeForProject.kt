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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeAlternative
import com.scouts.kitchenplaner.ui.view.CardState
import com.scouts.kitchenplaner.ui.view.ContentBox
import com.scouts.kitchenplaner.ui.view.ExpandableCard
import com.scouts.kitchenplaner.ui.view.recipes.IngredientsInput
import com.scouts.kitchenplaner.ui.view.recipes.InstructionInput
import com.scouts.kitchenplaner.ui.viewmodel.RecipeForCookingViewModel

/**
 * Top-Level Composable for displaying a recipe for Cooking. On this screen all quantities are
 * calculated for the correct number of people and the recipe's instructions as well as any
 * alternative recipes are displayed.
 *
 * @param project The project for which the recipe is being cooked
 * @param mealSlot The meal slot at which the recipe is being cooked
 * @param recipeID The ID of the recipe being cooked
 * @param onNavigateToAlternative Callback function to navigate to an alternative recipe
 * @param onNavigateToRecipeDetails Callback function to navigate to the screen for editing this
 *                                  recipe
 * @param recipeViewModel The viewmodel for accessing any relevant data
 */
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

        Column (modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipeForCooking.name,
                    fontSize = 8.em,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                VerticalDivider(modifier = Modifier.padding(end = 5.dp))

                IconButton(onClick = { onNavigateToRecipeDetails(recipeID) }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Recipe",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "Anzahl Personen: ${recipeForCooking.people}",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            IngredientsInput(
                ingredientGroups = recipeForCooking.ingredientGroups,
                editable = false,
                modifier = Modifier.padding(10.dp)
            )

            InstructionInput(
                instructions = recipeForCooking.instructions,
                editable = false,
                modifier = Modifier.padding(10.dp)
            )

            DisplayAlternatives(
                alternatives = recipeForCooking.alternatives,
                modifier = Modifier.padding(10.dp),
                onNavigateToAlternative = onNavigateToAlternative
            )
        }
    } else {
        Text("Waiting for the recipe to be loaded")
    }
}

/**
 * Composable for displaying the alternative recipes of a recipe that is being shown for cooking
 *
 * @param alternatives The alternative recipes that should be displayed
 * @param onNavigateToAlternative Callback function to navigate to an alternative recipe
 * @param modifier Compose modifier object that should be applied to the outermost container of this
 *                 Composable
 */
@Composable
fun DisplayAlternatives(
    alternatives: List<RecipeAlternative>,
    onNavigateToAlternative: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    ContentBox(
        title = "Alternativ-Rezepte",
        modifier = modifier
    ) {
        alternatives.forEach {
            DisplayAlternativeRecipe(it, onNavigateToAlternative)
        }
    }
}

/**
 * Composable for displaying a single alternative recipe as an expandable card. Per default, only
 * the name is displayed, but the card can be expanded to also show what allergens are covered by
 * the alternative compared to the main recipe
 *
 * @param alternative The alternative recipe that is to be displayed
 * @param onNavigateToAlternative Callback function for navigating to the alternative recipe
 */
@Composable
fun DisplayAlternativeRecipe(
    alternative: RecipeAlternative,
    onNavigateToAlternative: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val cardState = CardState(
        title = alternative.name,
        onDelete = {},
        toBeDeleted = false,
    ) {
        Column {
            alternative.coveredAllergens.forEach {
                Text("${it.allergen}: ${it.type.name}")
            }
            Button(onClick = { onNavigateToAlternative(alternative.id) }) {
                Text("Zum Rezept")
            }
        }
    }
    ExpandableCard(
        expanded = expanded,
        onCardArrowClick = { expanded = true },
        onTitleClick = { expanded = true },
        cardState = cardState
    )
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
