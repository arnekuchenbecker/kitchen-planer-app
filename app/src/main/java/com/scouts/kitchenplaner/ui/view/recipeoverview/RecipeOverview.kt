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

package com.scouts.kitchenplaner.ui.view.recipeoverview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.ui.OverviewField
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.viewmodel.RecipeSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverview(
    onNavigationCreateRecipe: () -> Unit,
    onNavigateToDetailedRecipe: (Long) -> Unit,
    viewModel: RecipeSelectionViewModel = hiltViewModel()
) {
    var recipeID by remember { mutableStateOf(0f) }
    Column {
        Text(text = "This is the recipe overview, where all recipes are displayed")
        Text(text = "available Links to other sides are: ")
        Row {
            Text("RecipeCreation")
            Button(onClick = onNavigationCreateRecipe) {

            }
        }
        Row {
            Text("ProjectDetails")
            Slider(
                modifier = Modifier.fillMaxWidth(0.3f),
                value = recipeID,
                onValueChange = { recipeID = it },
                valueRange = 1f..5f,
                steps = 5
            )
            Button(onClick = { onNavigateToDetailedRecipe(recipeID.toLong()) }) {}
        }

    }

    val recipes by viewModel.recipes.collectAsState(initial = listOf())
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Rezepte") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigationCreateRecipe,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                text = { Text("neues Rezept") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "neues Rezept erstellen"
                    )
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it))
        LazyColumnWrapper(content = recipes, DisplayContent = { stub, _ ->
            OverviewField(
                text = stub.name,
                imageUri = stub.imageURI,
                imageDescription = "Rezept bild",
                onClick = { onNavigateToDetailedRecipe(stub.id ?: 0) }
            )
        }, DisplayLast = { stub, _ ->
            OverviewField(
                text = stub.name,
                imageUri = stub.imageURI,
                imageDescription = "Rezept bild",
                onClick = { onNavigateToDetailedRecipe(stub.id ?: 0) }
            )
            //To allow scrolling stuff from behind the FAB
            Spacer(modifier = Modifier.height(75.dp))
        }, DisplayEmpty = { Text("Keine Rezepte") })


    }
}