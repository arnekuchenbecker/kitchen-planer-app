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

package com.scouts.kitchenplaner.ui.view.shoppinglistcreation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.model.entities.MealPlan
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.ui.state.ShoppingListEntryState
import com.scouts.kitchenplaner.ui.theme.GreyAir
import com.scouts.kitchenplaner.ui.viewmodel.CreateShoppingListViewModel
import java.text.DecimalFormat

/**
 * Top-Level Composable for creating a new shopping list
 *
 * @param project The project for which the shopping list is being created
 * @param onNavigateToShoppingList Callback function for navigating to the newly created shopping
 *                                 list
 * @param viewModel The view model used for interacting with the rest of the application
 */
@Composable
fun ShoppingListCreation(
    project: Project,
    onNavigateToShoppingList: (Long) -> Unit,
    viewModel: CreateShoppingListViewModel = hiltViewModel()
) {
    val navigateID by viewModel.navigateTo.collectAsState()

    navigateID?.let {
        onNavigateToShoppingList(it)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.createShoppingList(project) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Create Shopping List"
                    )
                },
                text = { Text("Fertig") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(10.dp),
                value = viewModel.state.name,
                onValueChange = {
                    viewModel.state.name = it
                },
                maxLines = 1,
                label = {
                    Text("Name")
                }
            )

            DisplayStaticEntries(
                entries = viewModel.state.staticItems,
                mealPlan = project.mealPlan,
                onAddEntry = viewModel::addStaticEntry
            )

            HorizontalDivider()

            DisplayDynamicEntries(
                entries = viewModel.state.dynamicItems,
                mealPlan = project.mealPlan,
                setEntries = viewModel::setDynamicEntries
            )

            //To allow scrolling stuff from behind the FAB
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

/**
 * Composable for displaying the static entries that have so far been added to the shopping list
 *
 * @param entries The static entries added so far
 * @param mealPlan The MealPlan of the project this shopping list belongs to
 * @param onAddEntry Callback function for adding a new static entry taking the name, unit and
 *                   amount of the new entry as parameters
 */
@Composable
fun DisplayStaticEntries(
    entries: List<ShoppingListEntryState>,
    mealPlan: MealPlan,
    onAddEntry: (String, String, Double) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Button(onClick = { showDialog = true }) {
        Text("Eintrag hinzufügen")
    }

    entries.forEach { entryState ->
        DisplayShoppingListEntry(entryState, mealPlan) { entryState.enabled = it }
    }

    if (showDialog) {
        StaticEntryAdderDialog(onDismissRequest = { showDialog = false }, onAddEntry = onAddEntry)
    }
}

/**
 * Composable for displaying all dynamic entries added to the shopping list so far.
 *
 * @param entries The dynamic entries added to the shopping list so far
 * @param mealPlan The MealPlan of the project this shopping list belongs to
 * @param setEntries Callback function for adding dynamic entries for the given list of meal slots
 *                   and recipes
 */
@Composable
fun DisplayDynamicEntries(
    entries: List<ShoppingListEntryState>,
    mealPlan: MealPlan,
    setEntries: (List<Pair<MealSlot, RecipeStub>>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    entries.forEach { entryState ->
        DisplayShoppingListEntry(entryState, mealPlan) { entryState.enabled = it }
    }

    Button(onClick = { showDialog = true }) {
        Text("Einträge für Rezepte...")
    }

    if (showDialog) {
        DynamicEntryAdderDialog(
            mealPlan = mealPlan,
            onDismissRequest = { showDialog = false },
            setEntries = setEntries
        )
    }
}

/**
 * Composable for displaying a single shopping list entry (regardless if it's dynamic or static)
 *
 * @param entryState The entry that should be displayed
 * @param mealPlan The MealPlan of the project this shopping list belongs to
 * @param onToggleEntry Callback function for toggling an entry in a shopping list
 */
@Composable
fun DisplayShoppingListEntry(
    entryState: ShoppingListEntryState,
    mealPlan: MealPlan,
    onToggleEntry: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val textStyle = if (entryState.enabled) {
            TextStyle()
        } else {
            TextStyle(color = GreyAir, textDecoration = TextDecoration.LineThrough)
        }

        val format = DecimalFormat("#.###")

        Checkbox(checked = entryState.enabled, onCheckedChange = onToggleEntry)

        Text(text = entryState.item.name, style = textStyle)

        Spacer(modifier = Modifier.weight(1.0f))

        Text(
            text = "${format.format(entryState.item.getAmount(mealPlan))} ${entryState.item.unit}",
            style = textStyle
        )
    }
}