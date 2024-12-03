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

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Dropdownmenu for selecting a meal from the given list of meals
 *
 * @param meals List of possible meals
 * @param selectedText The text displayed in the anchoring text field of this menu
 * @param expanded Whether the drop down menu is expanded
 * @param onExpandedChange Callback function for changing whether the menu is expanded
 * @param onSelectMeal Callback function for selecting a meal from the list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealSelectionMenu(
    meals: List<String>,
    selectedText: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelectMeal: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            meals.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelectMeal(it)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}