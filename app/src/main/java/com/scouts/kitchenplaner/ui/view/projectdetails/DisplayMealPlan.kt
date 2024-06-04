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

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapHorizontalCircle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.scouts.kitchenplaner.model.entities.AllergenCheck
import com.scouts.kitchenplaner.model.entities.AllergenMealCover
import com.scouts.kitchenplaner.model.entities.MealPlan
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.toDateString
import com.scouts.kitchenplaner.ui.theme.KitchenPlanerTheme
import com.scouts.kitchenplaner.ui.view.CardState
import com.scouts.kitchenplaner.ui.view.DeleteButton
import com.scouts.kitchenplaner.ui.view.ExpandableCard
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

/**
 * Composable displaying a meal plan to the user
 *
 * @param mealSlots List of all meal slots that should be displayed
 * @param mealPlan Meal plan containing which recipe(s) should be cooked and how many persons are
 *                 present for each meal slot
 * @param getAllergenCheck Callback function to obtain an allergen check for a specified meal slot
 * @param onSwap Callback function to swap the recipes of two meal slots
 * @param onShowRecipe Callback function to show a specified recipe for cooking
 * @param displayRecipeSelectionDialog Callback function to display the dialog for selecting a
 *                                     recipe
 * @param onDeleteRecipe Callback function for removing a recipe from a meal slot
 * @param modifier Compose modifier object that should be applied to the outermost container of this
 *                 composable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayMealPlan(
    mealSlots: List<MealSlot>,
    mealPlan: MealPlan,
    getAllergenCheck: (MealSlot) -> StateFlow<AllergenCheck>,
    onSwap: (MealSlot, MealSlot) -> Unit,
    onShowRecipe: (RecipeStub, MealSlot) -> Unit,
    displayRecipeSelectionDialog: (MealSlot, RecipeStub?) -> Unit,
    onDeleteRecipe: (MealSlot, RecipeStub?) -> Unit,
    modifier: Modifier = Modifier
) {
    var firstSwap by remember { mutableStateOf<MealSlot?>(null) }
    val expandedCards = remember { mutableStateMapOf<MealSlot, Boolean>() }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        if (mealSlots.isNotEmpty()) {
            mealSlots.groupBy { it.date }.forEach { (date, slots) ->
                stickyHeader(key = date) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = date.time.toDateString(),
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.Center),
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                items(items = slots, key = { it }) { slot ->
                    val planSlot = mealPlan[slot]

                    fun onSwapItems() {
                        firstSwap = if (firstSwap == null) {
                            slot
                        } else if (firstSwap != slot) {
                            onSwap(firstSwap ?: return, slot)
                            null
                        } else {
                            null
                        }
                    }

                    val coverFlow = remember { getAllergenCheck(slot) }
                    val cover by coverFlow.collectAsState()

                    MealDisplayItem(
                        persons = planSlot.second,
                        recipes = planSlot.first,
                        slot = slot,
                        cover = cover.mealCover,
                        expanded = expandedCards[slot] ?: false,
                        toggleExpanded = {
                            expandedCards[slot] = !(expandedCards[slot] ?: false)
                        },
                        onSwap = { onSwapItems() },
                        onDeleteRecipe = { onDeleteRecipe(slot, it) },
                        onShowRecipe = onShowRecipe,
                        displayRecipeSelectionDialog = {
                            displayRecipeSelectionDialog(slot, it)
                        },
                        toBeSwapped = firstSwap == slot
                    )
                }
            }
        } else {
            item(key = null) { Text(text = "Dieses Projekt hat keine Mahlzeiten...") }
        }
    }
}

/**
 * Composable for displaying information about a specific meal slot. Can be expanded to show more
 * detailed information (e.g. which recipes are selected)
 *
 * @param persons The number of persons present at the meal slot
 * @param recipes A Pair containing the main recipe and a list of alternative recipes selected for
 *                the meal slot or null if no recipes have been selected yet
 * @param slot The meal slot for which the information is displayed
 * @param cover Information on whether all allergens are covered by the selected recipes
 * @param expanded Whether the expandable content should be displayed
 * @param toggleExpanded Callback function to toggle displaying the expandable content
 * @param onSwap Callback function for clicks on the swap button
 * @param toBeSwapped Whether the meal slot is selected to be swapped with another
 * @param onDeleteRecipe Callback function to remove a recipe from the meal slot
 * @param displayRecipeSelectionDialog Callback function to display the recipe selection dialog. If
 *                                     a recipe stub is passed to the function, the chosen recipe
 *                                     should replace that recipe, otherwise (i.e. if null is
 *                                     passed), the chosen recipe should be added to recipes of the
 *                                     meal slot
 * @param onShowRecipe Callback function for displaying the given recipe for cooking
 */
@Composable
fun MealDisplayItem(
    persons: Int,
    recipes: Pair<RecipeStub, List<RecipeStub>>?,
    slot: MealSlot,
    cover: AllergenMealCover,
    expanded: Boolean,
    toggleExpanded: () -> Unit,
    onSwap: () -> Unit,
    toBeSwapped: Boolean,
    onDeleteRecipe: (RecipeStub?) -> Unit,
    displayRecipeSelectionDialog: (RecipeStub?) -> Unit,
    onShowRecipe: (RecipeStub, MealSlot) -> Unit
) {
    ExpandableCard(
        expanded = expanded,
        onCardArrowClick = toggleExpanded,
        onTitleClick = toggleExpanded,
        cardState = CardState(
            title = "${slot.meal}\n$persons ${if (persons == 1) "Person" else "Personen"}",
            titleInteractions = {
                Row(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (cover) {
                        AllergenMealCover.COVERED -> Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Allergen requirements fulfilled",
                            tint = Color(red = 0f, blue = 0.2f, green = 0.7f)
                        )

                        AllergenMealCover.UNKNOWN -> Icon(
                            imageVector = Icons.Filled.QuestionMark,
                            contentDescription = "Allergen requirements may not be fulfilled",
                            tint = Color(red = 1f, green = 0.7f, blue = 0f)
                        )

                        AllergenMealCover.NOT_COVERED -> Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Allergen requirements not fulfilled",
                            tint = Color.Red
                        )
                    }

                    val buttonColors = if (expanded) {
                        IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    } else {
                        IconButtonDefaults.iconButtonColors()
                    }

                    IconButton(onClick = onSwap, colors = buttonColors) {
                        if (toBeSwapped) {
                            Icon(
                                imageVector = Icons.Filled.SwapHorizontalCircle,
                                contentDescription = "Swap meals"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.SwapHoriz,
                                contentDescription = "Swap meals"
                            )
                        }
                    }
                }
            },
            onDelete = {},
            toBeDeleted = false,
            contentModifier = Modifier
        ) {
            if (recipes == null) {
                NoRecipesSelected {
                    displayRecipeSelectionDialog(null)
                }
            } else {
                DisplayRecipesForMeal(
                    recipes = recipes,
                    onExchange = { toExchange ->
                        displayRecipeSelectionDialog(toExchange)
                    },
                    onDelete = { toDelete ->
                        onDeleteRecipe(toDelete)
                    },
                    onAddAlternative = {
                        displayRecipeSelectionDialog(null)
                    },
                    onShowRecipe = { onShowRecipe(it, slot) }
                )
            }
        }
    )
}

/**
 * UI Element that should be displayed if no recipe has yet been selected for a meal slot.
 *
 * @param onClick Callback function for displaying the recipe selection dialog in order to select a
 *                main recipe for this slot
 */
@Composable
fun NoRecipesSelected(
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Dieser Mahlzeit wurden noch keine Rezepte zugeordnet.")
        HorizontalDivider()
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add main recipe")
        }
    }
}

/**
 * UI Element displaying the recipes assigned to a meal slot
 *
 * @param recipes Pair containing the main recipe and a list of alternative recipes assigned to the
 *                meal slot
 * @param onExchange Callback function to exchange a recipe for another
 * @param onDelete Callback function to remove a recipe from the meal slot
 * @param onAddAlternative Callback function to display the recipe selection dialog in order to
 *                         add an alternative recipe
 * @param onShowRecipe Callback function to display the given recipe for cooking
 */
@Composable
fun DisplayRecipesForMeal(
    recipes: Pair<RecipeStub, List<RecipeStub>>,
    onExchange: (RecipeStub) -> Unit,
    onDelete: (RecipeStub?) -> Unit,
    onAddAlternative: () -> Unit,
    onShowRecipe: (RecipeStub) -> Unit
) {
    Column {
        var editMain by remember { mutableStateOf(false) }

        Text("Hauptrezept:")
        HorizontalDivider()
        //TODO Dialog --> this will remove all recipe from this meal slot if this is the main recipe
        DisplayRecipe(
            recipes.first,
            editMain,
            onExchange,
            { onDelete(null) },
            onShowRecipe
        ) { editMain = !editMain }
        HorizontalDivider(thickness = 3.dp)
        Text("Alternativrezepte:")
        HorizontalDivider()
        recipes.second.forEach {
            var editAlternative by remember { mutableStateOf(false) }
            DisplayRecipe(
                it,
                editAlternative,
                onExchange,
                onDelete,
                onShowRecipe
            ) { editAlternative = !editAlternative }
        }
        HorizontalDivider()
        IconButton(
            onClick = onAddAlternative,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new alternative recipe")
        }
    }
}

/**
 * Composable for displaying a recipe in the meal plan
 *
 * @param recipe Information about the displayed recipe
 * @param editing Whether to display UI elements to edit this recipe
 * @param onExchange Callback function to exchange this recipe for another
 * @param onDelete Callback function to remove this recipe from its meal slot
 * @param onDisplayRecipe Callback function to display this recipe for cooking
 * @param onClick Callback function to toggle editing this recipe
 */
@Composable
fun DisplayRecipe(
    recipe: RecipeStub,
    editing: Boolean,
    onExchange: (RecipeStub) -> Unit,
    onDelete: (RecipeStub) -> Unit,
    onDisplayRecipe: (RecipeStub) -> Unit,
    onClick: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick() }
                .height(45.dp)
        ) {
            if (recipe.imageURI == Uri.EMPTY) {
                Icon(
                    modifier = Modifier
                        .height(45.dp)
                        .aspectRatio(1.0f)
                        .padding(start = 5.dp),
                    imageVector = Icons.Filled.HideImage,
                    contentDescription = "Projektplatzhalter"
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .height(45.dp)
                        .aspectRatio(1f)
                        .padding(start = 5.dp),
                    model = recipe.imageURI,
                    contentDescription = "Image for ${recipe.name}"
                )
            }
            Spacer(modifier = Modifier.weight(0.5f))
            Text(recipe.name)
            Spacer(modifier = Modifier.weight(0.5f))
        }
        if (editing) {
            HorizontalDivider()
            RecipeInteractions(
                onDisplayRecipe = { onDisplayRecipe(recipe) },
                onExchange = { onExchange(recipe) },
                onDelete = { onDelete(recipe) }
            )
        }
    }
}

/**
 * UI Elements to interact with a recipe in a meal plan. Contains buttons for showing a recipe for
 * cooking, exchanging the recipe and removing the recipe from the meal slot.
 * 
 * @param onDisplayRecipe Callback function for displaying the recipe for cooking
 * @param onExchange Callback function for exchanging the recipe for another
 * @param onDelete Callback function for removing the recipe from the meal slot
 */
@Composable
fun RecipeInteractions(
    onDisplayRecipe: () -> Unit,
    onExchange: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = onDisplayRecipe) {
            Text("Anzeigen")
        }

        Button(onClick = onExchange) {
            Text("Ändern")
        }

        DeleteButton(onClick = onDelete)
    }
}

@Preview(showBackground = true)
@Composable
fun MealDisplayItemPreview() {
    KitchenPlanerTheme(dynamicColor = false) {
        MealDisplayItem(
            persons = 17,
            recipes = null,
            slot = MealSlot(Date(0), "Frühstück"),
            cover = AllergenMealCover.UNKNOWN,
            expanded = true,
            toggleExpanded = {},
            onSwap = {},
            onDeleteRecipe = {},
            onShowRecipe = { _, _ -> },
            displayRecipeSelectionDialog = {},
            toBeSwapped = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoRecipesPreview() {
    KitchenPlanerTheme(dynamicColor = false) {
        NoRecipesSelected({})
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayRecipesPreview() {
    KitchenPlanerTheme(dynamicColor = false) {
        DisplayRecipesForMeal(
            recipes = Pair(
                RecipeStub(name = "Rezept1", imageURI = Uri.EMPTY), listOf(
                    RecipeStub(name = "Rezept2", imageURI = Uri.EMPTY),
                    RecipeStub(name = "Rezept3", imageURI = Uri.EMPTY)
                )
            ), {}, {}, {}, {}
        )
    }
}
