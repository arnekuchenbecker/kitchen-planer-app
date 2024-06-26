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

package com.scouts.kitchenplaner.ui.view.createrecipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.ui.view.ContentBox
import com.scouts.kitchenplaner.ui.view.Headline
import com.scouts.kitchenplaner.ui.state.RecipeAllergenState
import com.scouts.kitchenplaner.ui.view.DeleteButton

/**
 * Display UI Elements for entering allergens when creating a recipe.
 *
 * @param modifier Any composable modifiers that should be applied to this Composable
 * @param allergens State object containing allergens added so far
 */
@Composable
fun AllergenInput(
    modifier: Modifier = Modifier,
    allergens: RecipeAllergenState
) {
    ContentBox(
        title = "Allergene",
        modifier = modifier
    ) {
        AllergenCategoryInput(
            title = { Headline(text = "Enthält Allergene") },
            content = allergens.allergens,
            onAdd = { allergens.allergens.add(it) },
            onDelete = { allergen -> allergens.allergens.removeAll { it == allergen } }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

        AllergenCategoryInput(
            title = { Headline("Enthält Spuren") },
            content = allergens.traces,
            onAdd = { allergens.traces.add(it) },
            onDelete = { allergen -> allergens.traces.removeAll { it == allergen } }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

        AllergenCategoryInput(
            title = { Headline("Enthält nicht") },
            content = allergens.freeOf,
            onAdd = { allergens.freeOf.add(it) },
            onDelete = { allergen -> allergens.traces.removeAll { it == allergen } }
        )
    }
}

/**
 * Display UI elements for adding or removing allergens to a specific category
 *
 * @param title A Composable describing the category the allergens should be added to
 * @param content The allergens added to this category so far
 * @param onAdd Callback function for adding a new allergen
 * @param onDelete Callback function for deleting an existing allergen
 */
@Composable
fun AllergenCategoryInput(
    title: @Composable () -> Unit,
    content: List<String>,
    onAdd: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Column {
        title()
        var newAllergen by remember { mutableStateOf("") }
        OutlinedTextField(
            value = newAllergen,
            onValueChange = { newAllergen = it },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        onAdd(newAllergen)
                        newAllergen = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create new ingredient group"
                    )
                }
            },
            label = { Text("Allergen hinzufügen") },
            modifier = Modifier.fillMaxWidth()
        )

        content.forEach {
            var toBeDeleted by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(45.dp)
                    .clickable { toBeDeleted = !toBeDeleted },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = it)
                if (toBeDeleted) {
                    DeleteButton(onClick = { onDelete(it) })
                }
            }
        }
    }
}