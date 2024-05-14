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

package com.scouts.kitchenplaner.ui.view.projectsettingsdialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.ui.view.DeleteButton
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper

/**
 * Dialog for changing the meals of a project
 *
 * @param onDismissRequest Callback function for closing the dialog
 * @param meals Current List of meals
 * @param onMealAdd Callback function for adding a meal at the specified index
 * @param onMealRemove Callback function for removing a meal
 */
@Composable
fun MealChangeDialog(
    onDismissRequest: () -> Unit,
    meals: List<String>,
    onMealAdd: (String, Int) -> Unit,
    onMealRemove: (String) -> Unit
) {
    SettingDialog(
        onDismissRequest = onDismissRequest,
        title = "Mahlzeiten ändern",
        onConfirm = { /* Nothing to do here */ }
    ) {
        var toDelete by remember { mutableIntStateOf(-1) }
        var showAddDialog by remember { mutableStateOf(false) }

        Button(
            onClick = { showAddDialog = true }
        ) {
            Text("Neue Mahlzeit hinzufügen")
        }

        LazyColumnWrapper(
            content = meals,
            DisplayContent = { meal, index ->
                Surface(
                    onClick = {
                        toDelete = if (toDelete == index) {
                            -1
                        } else {
                            index
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(7.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(45.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = meal)
                        if (toDelete == index) {
                            DeleteButton(
                                onClick = {
                                    toDelete = -1
                                    onMealRemove(meal)
                                }
                            )
                        }
                    }
                }
            },
            DisplayEmpty = {
                Text(text = "Dieses Projekt hat keine Mahlzeiten.")
            }
        )

        if (showAddDialog) {
            MealAddDialog(
                onDismissRequest = { showAddDialog = false },
                onAddMeal = onMealAdd,
                meals = meals
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealAddDialog(
    onDismissRequest: () -> Unit,
    onAddMeal: (String, Int) -> Unit,
    meals: List<String>
) {
    var expandDropDown by remember { mutableStateOf(false) }
    var addFieldText by remember { mutableStateOf("") }
    var addBefore by remember { mutableIntStateOf(-1) }

    SettingDialog(
        onDismissRequest = onDismissRequest,
        title = "Neue Mahlzeit hinzufügen",
        onConfirm = { onAddMeal(addFieldText, addBefore) }
    ) {
        ExposedDropdownMenuBox(
            expanded = expandDropDown,
            onExpandedChange = { expandDropDown = it }
        ) {
            OutlinedTextField(
                value = if (addBefore >= 0) meals[addBefore] else "Am Ende",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandDropDown) },
                modifier = Modifier.menuAnchor(),
                label = { Text("Einfügen vor...") }
            )
            ExposedDropdownMenu(
                expanded = expandDropDown,
                onDismissRequest = { expandDropDown = false }
            ) {
                meals.forEachIndexed { index, meal ->
                    DropdownMenuItem(
                        text = { Text(meal) },
                        onClick = {
                            addBefore = index
                            expandDropDown = false
                        }
                    )
                }

                DropdownMenuItem(
                    text = { Text("Am Ende") },
                    onClick = {
                        addBefore = -1
                        expandDropDown = false
                    }
                )
            }
        }
        TextField(
            label = { Text("Neue Mahlzeit") },
            value = addFieldText,
            onValueChange = { addFieldText = it },
            singleLine = true
        )
    }
}