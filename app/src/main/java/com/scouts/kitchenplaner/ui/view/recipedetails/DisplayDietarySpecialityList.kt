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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper

/**
 *  This composable provides a representation of all current dietary specialities (within one category).
 *  If the edit mode is activated specialities can be added or deleted
 *
 *  @param editable  A function that indicates whether the list is editable
 *  @param allergenList The list which contains all dietary specialities
 *  @param add A function that adds a new speciality to the list
 *  @param delete A function that deletes a speciality from the list
 *
 */
@Composable
fun DisplayDietarySpecialityList(
    editable: () -> Boolean,
    allergenList: List<String>,
    add: (String) -> Unit,
    delete: (String) -> Unit,
) {

    var text by remember { mutableStateOf("") };
    Column() {
        if (editable()) {
            TextField(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                value = text,
                label = { Text("neue Ernährungsbesonderheit") },
                onValueChange = { text = it },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        add(text);
                        text = "";
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add, contentDescription = "Add new allergen"
                        )
                    }
                },
            )
            HorizontalDivider()
        }
        LazyColumnWrapper(content = allergenList, DisplayContent = { allergen, _ ->
            if (editable()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(allergen)
                    IconButton(onClick = {
                        delete(allergen)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Allergen"
                        )
                    }
                }
            } else {
                Text(allergen)
            }
        }, DisplayEmpty = { Text("Noch nichts eingetragen", modifier = Modifier.padding(5.dp)) })
    }
}