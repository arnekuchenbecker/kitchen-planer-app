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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.ui.view.Header
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.view.OverviewField
import com.scouts.kitchenplaner.ui.viewmodel.RecipeSelectionViewModel

@Composable
fun RecipeOverview(
    onNavigationCreateRecipe: () -> Unit,
    onNavigateToDetailedRecipe: (Long) -> Unit,
    viewModel: RecipeSelectionViewModel = hiltViewModel()
) {
    val recipes by viewModel.recipes.collectAsState(initial = listOf())
    Scaffold(topBar = {
        Header("Rezepte")
    }, floatingActionButton = {
        ExtendedFloatingActionButton(onClick = onNavigationCreateRecipe,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            text = { Text("Neues Rezept") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "neues Rezept erstellen"
                )
            })
    }) {
        Box(modifier = Modifier.padding(it)) {
            LazyColumnWrapper(modifier = Modifier.padding(10.dp),
                content = recipes, DisplayContent = { stub, _ ->
                    OverviewField(text = stub.name,
                        imageUri = stub.imageURI,
                        imageDescription = "Rezept bild",
                        onClick = { onNavigateToDetailedRecipe(stub.id ?: 0) })
                }, DisplayLast = { stub, _ ->
                    OverviewField(text = stub.name,
                        imageUri = stub.imageURI,
                        imageDescription = "Rezept bild",
                        onClick = { onNavigateToDetailedRecipe(stub.id ?: 0) })
                    //To allow scrolling stuff from behind the FAB
                    Spacer(modifier = Modifier.height(75.dp))
                }, DisplayEmpty = { Text("Keine Rezepte") })
        }
    }
}