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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.listDatesUntil
import com.scouts.kitchenplaner.model.entities.MealPlan
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.toDateString
import com.scouts.kitchenplaner.ui.view.DockedDatePicker
import com.scouts.kitchenplaner.ui.view.NumberFieldType
import com.scouts.kitchenplaner.ui.view.OutlinedNumberField
import java.util.Date

/**
 * Dialog for adding a new static entry to a shopping list
 *
 * @param onDismissRequest Callback function for closing the dialog upon user request
 * @param onAddEntry Callback function for adding a new static entry taking the name, unit and
 *                   amount of the new entry as parameters
 */
@Composable
fun StaticEntryAdderDialog(
    onDismissRequest: () -> Unit,
    onAddEntry: (String, String, Double) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(15.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var name by remember { mutableStateOf("") }
                var unit by remember { mutableStateOf("") }
                var amount by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Zutat") }
                )

                OutlinedNumberField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Menge") },
                    type = NumberFieldType.FLOAT
                )

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Einheit") }
                )

                Button(
                    modifier = Modifier.padding(top = 5.dp),
                    onClick = {
                        if (name.isNotBlank() && amount.toDouble() != 0.0) {
                            onAddEntry(name, unit, amount.toDouble())
                        }
                        onDismissRequest()
                    }
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add ingredient")

                    Text("Eintrag hinzufügen")
                }
            }
        }
    }
}

/**
 * Dialog for adding dynamic entries to a shopping list based on a start and end meal slot
 *
 * @param mealPlan The MealPlan of the project the shopping list belongs to
 * @param onDismissRequest Callback function for closing the dialog upon user request
 * @param setEntries Callback function for adding dynamic entries for the given list of meal slots
 *  *                and recipes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicEntryAdderDialog(
    mealPlan: MealPlan,
    onDismissRequest: () -> Unit,
    setEntries: (List<Pair<MealSlot, RecipeStub>>) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(15.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val startDate = rememberDatePickerState()
                val endDate = rememberDatePickerState()

                var expandStartMeal by remember { mutableStateOf(false) }
                var expandEndMeal by remember { mutableStateOf(false) }

                var startMeal by remember { mutableStateOf("Mahlzeit auswählen...") }
                var endMeal by remember { mutableStateOf("Mahlzeit auswählen...") }

                DockedDatePicker(
                    modifier = Modifier
                        .padding(5.dp)
                        .height(70.dp),
                    dateState = startDate,
                    displayText = startDate.selectedDateMillis?.toDateString() ?: "Kein Start-Datum ausgewählt",
                    label = "Start"
                )

                MealSelectionMenu(
                    meals = mealPlan.meals,
                    selectedText = startMeal,
                    expanded = expandStartMeal,
                    onExpandedChange = { expandStartMeal = it },
                    onSelectMeal = { startMeal = it }
                )

                DockedDatePicker(
                    modifier = Modifier
                        .padding(5.dp)
                        .height(70.dp),
                    dateState = endDate,
                    displayText = endDate.selectedDateMillis?.toDateString() ?: "Kein End-Datum ausgewählt",
                    label = "Ende"
                )

                MealSelectionMenu(
                    meals = mealPlan.meals,
                    selectedText = endMeal,
                    expanded = expandEndMeal,
                    onExpandedChange = { expandEndMeal = it },
                    onSelectMeal = { endMeal = it }
                )

                Button(
                    onClick = {
                        val start = startDate.selectedDateMillis?.let {  Date(it) }
                        val end = endDate.selectedDateMillis?.let {  Date(it) }
                        if (mealPlan.meals.contains(startMeal)
                            && mealPlan.meals.contains(endMeal)
                            && start != null && end != null
                            && (start.before(end) || start == end)
                        ) {
                            val mealSlots = mutableListOf<MealSlot>()
                            mealPlan.meals.forEachIndexed { index, meal ->
                                if (mealPlan.meals.indexOf(startMeal) <= index) {
                                    mealSlots.add(MealSlot(start, meal))
                                }
                                if (start != end && mealPlan.meals.indexOf(endMeal) >= index) {
                                    mealSlots.add(MealSlot(end, meal))
                                }
                                val otherMealSlots = start.listDatesUntil(end)
                                    .filter { it != start && it != end }
                                    .map { MealSlot(it, meal) }
                                mealSlots.addAll(otherMealSlots)
                            }

                            val stubs = mealSlots.map {
                                val recipePair = mealPlan[it].first
                                val recipes = recipePair?.let { (main, alternatives) ->
                                    listOf(main) + alternatives
                                } ?: listOf()
                                recipes.map { stub -> Pair(it, stub) }
                            }.flatten()

                            setEntries(stubs)
                            onDismissRequest()
                        }
                    }
                ) {
                    Text("Einträge hinzufügen!")
                }
            }
        }
    }
}