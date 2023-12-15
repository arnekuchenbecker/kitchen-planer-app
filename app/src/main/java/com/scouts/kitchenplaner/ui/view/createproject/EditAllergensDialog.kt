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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper


@Composable
fun EditAllergensDialog(onDismissRequest: () -> Unit, onAdd: (String, String, Boolean) -> Unit, onRemove: (String) -> Unit, onRemoveItem: (String, String, Boolean) -> Unit ,allergens: Map<String, List<Pair<String, Boolean>>>) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.fillMaxHeight(0.5f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(20.dp)) {
                var removingIndex by remember { mutableIntStateOf(-1) }

                AllergenAdder(onAdd = onAdd)

                Divider(thickness = 2.dp, modifier = Modifier.padding(vertical = 5.dp))

                LazyColumnWrapper(
                    content = allergens.toList(),
                    DisplayContent = { (name, allergens), index ->
                        AllergenCard(
                            name = name,
                            allergens = allergens,
                            onTitleClick = {
                                removingIndex = if (removingIndex == index) {
                                    -1
                                } else {
                                    index
                                }
                            },
                            onDelete = { onRemove(name) },
                            onItemDelete = {(allergen, traces) ->
                                onRemoveItem(name, allergen, traces)
                            },
                            toBeDeleted = removingIndex == index
                        )
                    },
                    DisplayEmpty = {
                        Box(
                            modifier = Modifier.padding(0.dp, 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text("Keine Intoleranten Personen")
                        }
                    }
                )
            }
        }
    }
}