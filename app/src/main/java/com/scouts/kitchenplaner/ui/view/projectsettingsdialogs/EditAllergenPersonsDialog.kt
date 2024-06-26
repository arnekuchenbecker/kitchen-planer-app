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

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.ui.state.AllergenPersonAdderState
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.view.allergendialog.AllergenCard
import com.scouts.kitchenplaner.ui.view.allergendialog.AllergenPersonAdder
import java.util.Date

/**
 * Dialog to edit allergen persons of a project
 *
 * @param onDismissRequest Callback function for closing the dialog
 * @param onRemovePerson Callback function for removing a person from the project
 * @param onRemoveAllergen Callback function for removing an allergen from a person
 * @param onAddAllergenPerson Callback function for adding an allergen Person
 * @param allergenPersons List of current allergen persons
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAllergenPersonsDialog(
    onDismissRequest: () -> Unit,
    onRemovePerson: (AllergenPerson) -> Unit,
    onRemoveAllergen: (AllergenPerson, Allergen) -> Unit,
    onAddAllergenPerson: (AllergenPerson) -> Unit,
    allergenPersons: List<AllergenPerson>
) {
    SettingDialog(
        onDismissRequest = onDismissRequest,
        title = "Allergische Personen ändern",
        onConfirm = { /* Nothing to do here */ }
    ) {
        var showAdderDialog by remember { mutableStateOf(false) }
        val adderState = remember { AllergenPersonAdderState() }
        val expandedCards = remember { mutableStateListOf<Int>() }
        Button(onClick = { showAdderDialog = true }) {
            Text(text = "Weitere Person hinzufügen")
        }

        LazyColumnWrapper(
            content = allergenPersons,
            DisplayContent = { person, index ->
                var personToBeDeleted by remember { mutableStateOf(false) }
                AllergenCard(
                    name = person.name,
                    allergens = person.allergens.map { Pair(it.allergen, it.traces) },
                    arrivalDate = person.arrivalDate.time,
                    arrivalMeal = person.arrivalMeal,
                    departureDate = person.departureDate.time,
                    departureMeal = person.departureMeal,
                    onTitleClick = { personToBeDeleted = !personToBeDeleted },
                    onDelete = { onRemovePerson(person) },
                    onItemDelete = { onRemoveAllergen(person, Allergen(it.first, it.second)) },
                    toBeDeleted = personToBeDeleted,
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
                Text(text = "Keine Intoleranten Personen")
            }
        )

        if (showAdderDialog) {
            AllergenPersonAdder(
                state = adderState,
                onAdd = {
                    onAddAllergenPerson(
                        AllergenPerson(
                            name = adderState.name,
                            allergens = adderState.allergens.map { Allergen(it.first, it.second) },
                            arrivalDate = Date(adderState.arrivalDate.selectedDateMillis ?: 0),
                            arrivalMeal = adderState.arrivalMeal,
                            departureDate = Date(adderState.departureDate.selectedDateMillis ?: 0),
                            departureMeal = adderState.departureMeal
                        )
                    )
                },
                onDismiss = {
                    showAdderDialog = false
                }
            )
        }
    }
}