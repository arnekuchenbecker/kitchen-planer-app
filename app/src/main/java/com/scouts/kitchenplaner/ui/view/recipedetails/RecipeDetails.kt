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

package com.scouts.kitchenplaner.ui.view.recipedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.ui.view.ContentBox
import com.scouts.kitchenplaner.ui.view.Header
import com.scouts.kitchenplaner.ui.view.Headline
import com.scouts.kitchenplaner.ui.view.PicturePicker
import com.scouts.kitchenplaner.ui.viewmodel.EditRecipeViewModel


@Composable
fun RecipeDetails(
    recipeID: Long, viewModel: EditRecipeViewModel = hiltViewModel()
) {

    var recipeInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = null) {
        viewModel.getRecipe(recipeID)
        recipeInitialized = true
    }
    if (recipeInitialized) {
        val recipe by viewModel.recipeFlow.collectAsState()

        Column {
            Header(title = recipe.name)
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                ContentBox(title = "Zutaten", modifier = Modifier.fillMaxWidth(0.45f)) {
                    recipe.ingredientGroups.forEach { group ->
                        Headline(group.name)
                        Column {
                            group.ingredients.forEach {
                                Row {
                                    Text(it.name)
                                    Text(it.amount.toString())
                                    Text(it.unit)
                                }
                            }
                        }
                    }
                }
                PicturePicker(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f),
                    onPathSelected = {},
                    path = recipe.imageURI
                )
            }
        }
        ContentBox(title = "Schritte") {
            //recipe.instructions.forEachIndexed()

        }
    }
}
