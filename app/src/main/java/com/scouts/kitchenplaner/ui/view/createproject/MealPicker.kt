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

package com.scouts.kitchenplaner.ui.view.createproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.view.ListEditHeader
import kotlin.math.min

/**
 * Section for adding meals to the project.
 * It contains the title and a button to add and delete meals.
 * Under it there is a list with all already added meals.
 *
 * @param modifier customisable modifier
 * @param onAdd Callback function for adding a new meal with the provided name
 * @param onRemove Callback function for removing the meal on the given index
 * @param meals All already created meals
 */
@Composable
fun MealPicker(
    modifier: Modifier = Modifier,
    onAdd: (String) -> Unit,
    onRemove: (Int) -> Unit,
    meals: List<String>,
    isError: Boolean = false,
    validate: () -> Unit = {}
) {
    var displayDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.heightIn(max = (200 + 20 * (1 + min(meals.size, 4))).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListEditHeader(onClick = { displayDialog = true }, title = "Mahlzeiten", isError)

        LazyColumnWrapper(
            content = meals,
            DisplayContent = { meal, _ ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(0.dp, 5.dp),
                        text = meal
                    )

                    HorizontalDivider(thickness = 1.dp)
                }
            }, DisplayLast = { meal, _ ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        modifier = Modifier
                            .padding(0.dp, 5.dp),
                        text = meal
                    )
                }
            }, DisplayEmpty = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        modifier = Modifier
                            .padding(0.dp, 5.dp),
                        text = "Keine Mahlzeiten",
                        color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
                    )
                }
            }
        )
    }

    if (displayDialog) {
        EditMealsDialog(
            onDismissRequest = {
                displayDialog = false
                validate()
            },
            onAdd = onAdd,
            onRemove = onRemove,
            meals = meals
        )
    }
}