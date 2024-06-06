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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.ui.state.AllergenPersonAdderState
import com.scouts.kitchenplaner.ui.view.DockedDatePicker

/**
 * Dialog for adding a new allergen person. It defines the layout of the dialog and checks if all relevant information are added.
 * The name, start and end date and meal have to be added and at least one allergen.
 *  Under the field for adding new allergens all current added allergens are displayed including if they are a trace.
 *
 *  @param state All current information over the allergen person to add
 *  @param onAdd Callback function to add a new allergen person  (This function doesn't have to close the dialog)
 *  @param onDismiss Callback function when the dialog is closed without adding the allergen person (This function should close the dialog)
 */
@Composable
fun AllergenPersonAdder(state: AllergenPersonAdderState, onAdd: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxHeight(0.5f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            val columnItemModifier = Modifier
                .padding(5.dp)
                .height(70.dp)
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                AllergenPersonInputs(state, columnItemModifier)

                HorizontalDivider()

                AllergenAddInputs(
                    onAdd = { allergen, traces ->
                        if (allergen != "") {
                            state.addAllergen(allergen, traces)
                        }
                    },
                    modifier = Modifier.padding(10.dp)
                )

                if (state.allergens.isNotEmpty()) {
                    HorizontalDivider()

                    Column {
                        state.allergens.forEach { (allergen, traces) ->
                            val text = if (traces) {
                                " (Spuren)"
                            } else ""
                            Text(modifier = Modifier.padding(10.dp), text = "$allergen$text")
                        }
                    }
                }

                HorizontalDivider()

                Button(
                    onClick = {
                        if (state.name != "" && state.allergens.isNotEmpty()) {
                            onAdd()
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Add allergic person"
                    )

                    Text("Person hinzufügen")
                }
            }
        }
    }
}

/**
 * Represents all input field for adding the meta data for a new allergen person.
 * These meta data contain the name, the arrival and departure date of the person and their start and departure meal.
 *
 * @param state The state which saves all already added inputs
 * @param modifier customisable modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllergenPersonInputs (
    state: AllergenPersonAdderState,
    modifier: Modifier = Modifier
) {
    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            modifier = Modifier.padding(10.dp),
            label = { Text("Name") },
            value = state.name,
            onValueChange = { state.name = it },
            singleLine = true
        )

        DockedDatePicker(
            modifier = modifier
                .padding(10.dp)
                .fillMaxWidth(),
            dateState = state.arrivalDate,
            displayText = state.arrivalDateString,
            label = "Ankunfts-Datum"
        )

        TextField(
            modifier = Modifier.padding(10.dp),
            label = { Text("Anwesend ab Mahlzeit...") },
            value = state.arrivalMeal,
            onValueChange = { state.arrivalMeal = it },
            singleLine = true
        )

        DockedDatePicker(
            modifier = modifier
                .padding(10.dp)
                .fillMaxWidth(),
            dateState = state.departureDate,
            displayText = state.departureDateString,
            label = "Abreise-Datum"
        )

        TextField(
            modifier = Modifier.padding(10.dp),
            label = { Text("Anwesend bis Mahlzeit...") },
            value = state.departureMeal,
            onValueChange = { state.departureMeal = it },
            singleLine = true
        )
    }
}

/**
 * Represents the block for adding new allergens.
 * It contains a field to add the new allergen and a checkbox to mark whether traces are relevant too.
 *
 * @param onAdd Callback function to add a new allergen (String) and whether traces are relevant
 * @param modifier customisable modifier
 */
@Composable
fun AllergenAddInputs(onAdd: (String, Boolean) -> Unit, modifier: Modifier = Modifier) {
    var allergen by remember { mutableStateOf("") }
    var traces by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth().padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.7f)) {
            TextField(
                singleLine = true,
                label = { Text("Allergen") },
                value = allergen,
                onValueChange = { allergen = it }
            )

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Spuren")

                Checkbox(
                    checked = traces,
                    onCheckedChange = { traces = it }
                )
            }
        }

        IconButton(
            onClick = {
                onAdd(allergen, traces)
                allergen = ""
                traces = false
            }
        ) {
            Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "Add allergen")
        }
    }
}
