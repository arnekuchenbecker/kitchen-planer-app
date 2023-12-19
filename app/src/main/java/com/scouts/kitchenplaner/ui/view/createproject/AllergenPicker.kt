/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023  Arne Kuchenbecker, Antonia Heiming
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.ui.state.AllergenPersonAdderState
import com.scouts.kitchenplaner.ui.state.AllergenPersonState
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.view.ListEditHeader
import kotlin.math.min


@Composable
fun AllergenPicker(
    modifier: Modifier = Modifier,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    onRemoveItem: (String, String, Boolean) -> Unit,
    allergens: List<AllergenPersonState>
) {
    var displayDialog by remember { mutableStateOf(false) }
    val dialogState by remember { mutableStateOf(AllergenPersonAdderState()) }
    Column (
        modifier = modifier.height((90 + 20 * (1 + min(allergens.size, 4))).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListEditHeader(
            onClick = { displayDialog = true },
            title = "Ernährungsbesonderheiten"
        )

        LazyColumnWrapper(
            content = allergens,
            DisplayContent = { person, _ ->
                AllergenListElement(name = person.name, displayDivider = true)
            },
            DisplayLast = { person, _ ->
                AllergenListElement(name = person.name, displayDivider = false)
            },
            DisplayEmpty = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        modifier = Modifier
                            .padding(0.dp, 5.dp),
                        text = "Keine Intoleranten Personen")
                }
            }
        )
    }

    if (displayDialog) {
        EditAllergensDialog(
            onDismissRequest = { displayDialog = false },
            onAdd = onAdd,
            onRemove = onRemove,
            onRemoveItem = onRemoveItem,
            allergens = allergens,
            adderState = dialogState
        )
    }
}

@Composable
fun AllergenListElement(name: String, displayDivider: Boolean) {
    Column (
        modifier = Modifier
            .fillMaxWidth(0.6f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 5.dp),
            text = name
        )

        if (displayDivider) {
            HorizontalDivider(thickness = 1.dp)
        }
    }
}