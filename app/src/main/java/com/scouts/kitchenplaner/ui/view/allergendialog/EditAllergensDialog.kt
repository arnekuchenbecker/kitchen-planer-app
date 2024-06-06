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

package com.scouts.kitchenplaner.ui.view.allergendialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.ui.state.AllergenPersonAdderState
import com.scouts.kitchenplaner.ui.state.AllergenPersonState
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper


/**
 * Dialog for a detailed view of allergen persons and to edit them.
 * It contains a button for adding new allergen persons and a list of all already added allergen persons.
 * For each allergen person their meta data is shown and their allergens can be displayed.
 * An allergen or a allergen person can be removed.
 *
 * @param onDismissRequest Callback function for closing the the dialog
 * @param onAdd Callback function for adding a new allergen person
 * @param onRemove Callback function for removing an allergen person
 * @param onRemoveItem Callback function for removing an allergen defined by its name (2) and if traces are relevant (3) from an allergen person (1)
 * @param onResetAdderState  Callback function for resetting all information currently saved in the adder state
 * @param allergens All currently available allergen persons
 * @param adderState State that contains all information about an allergen person during their creation
 */
@Composable
fun EditAllergensDialog(
    onDismissRequest: () -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    onRemoveItem: (String, String, Boolean) -> Unit,
    onResetAdderState: () -> Unit,
    allergens: List<AllergenPersonState>,
    adderState: AllergenPersonAdderState
) {
    Dialog(onDismissRequest = onDismissRequest) {
        var displayDialog by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier.fillMaxHeight(0.5f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxHeight()
            ) {
                var removingIndex by remember { mutableIntStateOf(-1) }
                val expandedCards = remember { mutableStateListOf<Int>() }

                Button(onClick = { displayDialog = true }) {
                    Text(text = "Neue Person hinzufügen")
                }

                if (displayDialog) {
                    AllergenPersonAdder(
                        state = adderState,
                        onAdd = onAdd,
                        onDismiss = {
                            displayDialog = false
                            onResetAdderState()
                        }
                    )
                }

                LazyColumnWrapper(
                    content = allergens,
                    DisplayContent = { person, index ->
                        AllergenCard(
                            name = person.name,
                            allergens = person.allergens,
                            arrivalDate = person.arrivalDateMillis,
                            arrivalMeal = person.arrivalMeal,
                            departureDate = person.departureDateMillis,
                            departureMeal = person.departureMeal,
                            onTitleClick = {
                                removingIndex = if (removingIndex == index) {
                                    -1
                                } else {
                                    index
                                }
                            },
                            onDelete = { onRemove(person.name) },
                            onItemDelete = {(allergen, traces) ->
                                onRemoveItem(person.name, allergen, traces)
                            },
                            toBeDeleted = removingIndex == index,
                            expand = expandedCards.contains(index),
                            toggleExpand = {
                                if (expandedCards.contains(index)) {
                                    expandedCards.remove(index)
                                } else {
                                    expandedCards.add(index)
                                }
                            }
                        )
                    },
                    DisplayEmpty = {
                        Box(
                            modifier = Modifier.padding(0.dp, 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text("Keine intoleranten Personen")
                        }
                    }
                )
            }
        }
    }
}