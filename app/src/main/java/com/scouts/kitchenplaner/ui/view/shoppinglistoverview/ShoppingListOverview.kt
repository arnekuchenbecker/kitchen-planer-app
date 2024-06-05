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

package com.scouts.kitchenplaner.ui.view.shoppinglistoverview

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.view.OverviewField
import com.scouts.kitchenplaner.ui.viewmodel.ShoppingListsOverviewViewModel

/**
 * Top-Level Composable for the shopping list overview screen. This screen displays all shopping
 * lists of a specific project.
 *
 * @param project The project for which the shopping lists should be displayed
 * @param onNavigateToShoppingList Callback function to navigate to the details view of a specific
 *                                 shopping list
 * @param onNavigateToCreateShoppingList Callback function to navigate to the screen for creating
 *                                       a new shopping list
 * @param viewModel The viewmodel to be used for interaction with the lower layers of the app
 */
@Composable
fun ShoppingListOverview(
    project: Project,
    onNavigateToShoppingList: (Long) -> Unit,
    onNavigateToCreateShoppingList: () -> Unit,
    viewModel: ShoppingListsOverviewViewModel = hiltViewModel()
) {
    val shoppingLists by viewModel.getShoppingLists(project).collectAsState()

    Scaffold (
        modifier = Modifier.padding(5.dp),
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateShoppingList) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add a new shoppin list")
            }
        }
    ) {
        LazyColumnWrapper(
            modifier = Modifier.padding(it),
            content = shoppingLists,
            DisplayContent = { stub, _ ->
                OverviewField(
                    modifier = Modifier.padding(5.dp),
                    text = stub.name,
                    onClick = { onNavigateToShoppingList(stub.id) },
                    displayImage = false
                )
            },
            DisplayEmpty = {
                Text("Bisher wurden keine Einkaufslisten angelegt.")
            }
        )
    }
}