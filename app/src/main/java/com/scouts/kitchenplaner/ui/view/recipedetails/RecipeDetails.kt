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

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.scouts.kitchenplaner.ui.view.ContentBox
import com.scouts.kitchenplaner.ui.view.Header
import com.scouts.kitchenplaner.ui.view.recipes.IngredientsInput
import com.scouts.kitchenplaner.ui.view.recipes.InstructionInput
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

        Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
            Header(title = recipe.name)


            Row(modifier = Modifier.padding(5.dp).fillMaxWidth()) {

                Column(Modifier.fillMaxWidth(0.5f)) {
                    Text(
                        "Für " + recipe.numberOfPeople + " Person(en)",
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )
                    HorizontalDivider()
                    Text(
                        text = recipe.description,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Left,
                        maxLines = 7,
                    )
                }
                AsyncImage(
                    model = recipe.imageURI,
                    contentDescription = "Recipe picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )


            }
            ContentBox(title = "Allergene", modifier = Modifier.padding(10 .dp)) {
                Column {
                    Text("Frei von: ")
                    recipe.freeOfAllergen.forEach { name ->
                        Text(name);
                    }
                    HorizontalDivider()
                    Text("Enthält: ")
                    recipe.allergens.forEach { name -> Text(name) }
                    HorizontalDivider()
                    Text("Enthält Spuren von: ")
                    recipe.traces.forEach { Text(it) }
                }


            }
            IngredientsInput(
                modifier = Modifier.padding(10 .dp),
                ingredientGroups = recipe.ingredientGroups,
                editable = false
            )
            InstructionInput(
                modifier = Modifier.padding(10 .dp),
                instructions = recipe.instructions,
                editable = false,
            )

        }

    }
}
