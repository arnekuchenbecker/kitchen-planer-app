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

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

@Composable
fun DisplayMealPlan(
    mealSlots: List<MealSlot>,
    mealPlan: MealPlan,
    getAllergenCheck: (MealSlot) -> StateFlow<AllergenCheck>,
    onSwap: (MealSlot, MealSlot) -> Unit
) {
    var firstSwap by remember { mutableStateOf<MealSlot?>(null) }
    LazyColumnWrapper(
        content = mealSlots,
        DisplayContent = { slot, _ ->
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

            MealDisplayItem(
                persons = planSlot.second,
                recipes = planSlot.first,
                slot = slot,
                cover = getAllergenCheck(slot).collectAsState().value.mealCover,
                onSwap = { onSwapItems() },
                toBeSwapped = firstSwap == slot
            )
        }
    ) {

    }
}

@Composable
fun NoRecipesSelected(
    onClick: () -> Unit
) {
    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Dieser Mahlzeit wurden noch keine Rezepte zugeordnet.")
        HorizontalDivider()
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add main recipe")
        }
    }
}

@Composable
fun MealDisplayItem(
    persons: Int,
    recipes: Pair<RecipeStub, List<RecipeStub>>?,
    slot: MealSlot,
    cover: AllergenMealCover,
    onSwap: () -> Unit,
    toBeSwapped: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    ExpandableCard(
        expanded = expanded,
        onCardArrowClick = { expanded = !expanded },
        onTitleClick = { expanded = !expanded },
        cardState = CardState(
            title = "${slot.date.time.toDateString()}, ${slot.meal}\n$persons ${if (persons == 1) "Person" else "Personen"}",
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

                    IconButton(onClick = onSwap) {
                        if (toBeSwapped) {
                            Icon(imageVector = Icons.Filled.SwapHorizontalCircle, contentDescription = "Swap meals")
                        } else {
                            Icon(imageVector = Icons.Filled.SwapHoriz, contentDescription = "Swap meals")
                        }
                    }
                }
            },
            onDelete = {},
            toBeDeleted = false,
            contentModifier = Modifier
        ) {
            if (recipes == null) {
                NoRecipesSelected({})
            } else {
                DisplayRecipesForMeal(recipes, {},{},{})
            }
        }
    )
}

@Composable
fun DisplayRecipesForMeal(
    recipes: Pair<RecipeStub, List<RecipeStub>>,
    onExchange: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onAddAlternative: () -> Unit
) {
    Column {
        var editMain by remember { mutableStateOf(false) }

        Text("Hauptrezept:")
        HorizontalDivider()
        DisplayRecipe(recipes.first, editMain, onExchange, onDelete) { editMain = !editMain }
        HorizontalDivider(thickness = 3.dp)
        Text("Alternativrezepte:")
        HorizontalDivider()
        recipes.second.forEach {
            var editAlternative by remember { mutableStateOf(false) }
            println("Displaying recipe ${it.name}")
            DisplayRecipe(it, editAlternative, onExchange, onDelete) { editAlternative = !editAlternative }
        }
        HorizontalDivider()
        IconButton(onClick = onAddAlternative, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new alternative recipe")
        }
    }
}

@Composable
fun DisplayRecipe(
    recipe: RecipeStub,
    editing: Boolean,
    onExchange: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
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
        if (!editing) {
            Text(recipe.name)
        } else {
            Row {
                Button(onClick = { onExchange(recipe.id ?: 0) }) {
                    Text("Ändern")
                }

                DeleteButton {
                    onDelete(recipe.id ?: 0)
                }
            }
        }
        Spacer(modifier = Modifier.weight(0.5f))
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
            onSwap = {},
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
            ), {}, {}, {}
        )
    }
}
