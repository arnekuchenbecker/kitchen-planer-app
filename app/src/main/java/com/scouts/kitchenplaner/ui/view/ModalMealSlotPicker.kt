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

package com.scouts.kitchenplaner.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.scouts.kitchenplaner.DateUtils
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalMealSlotPicker(
    modifier: Modifier = Modifier,
    meals: List<String>,
    selectedMeal: String,
    onSelectMeal: (String) -> Unit,
    datePlaceHolder: @Composable (() -> Unit)? = { Text("Datum") },
    state: DatePickerState
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var expandMenu by rememberSaveable { mutableStateOf(false) }
    val dateFormatter =
        remember { DatePickerDefaults.dateFormatter(selectedDateSkeleton = "ddMMy") }

    val fullscreen = LocalConfiguration.current.screenWidthDp.dp < 400.dp

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(5.dp)
                )
                .fillMaxWidth()
        ) {
            ClickableOutlinedTextField(
                value = printDate(state.selectedDateMillis),
                onClick = { showDatePicker = !showDatePicker },
                placeholder = datePlaceHolder,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            )

            MealSelectionMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, bottom = 5.dp),
                meals = meals,
                selectedText = selectedMeal,
                expanded = expandMenu,
                onExpandedChange = { expandMenu = it },
                onSelectMeal = onSelectMeal
            )
        }
    }

    if (showDatePicker) {
        if (fullscreen) {
            Popup(
                properties = PopupProperties(focusable = true, dismissOnBackPress = true),
                onDismissRequest = { showDatePicker = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    DatePicker(
                        state = state,
                        dateFormatter = dateFormatter,
                        title = null,
                        modifier = Modifier.fillMaxHeight(0.6f),
                        showModeToggle = false
                    )

                    HorizontalDivider()

                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.End)
                    ) {
                        IconButton(
                            onClick = {
                                state.selectedDateMillis = null
                                showDatePicker = false
                            }
                        ) {
                            Icon(Icons.Filled.Close, "Cancel")
                        }

                        IconButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Icon(Icons.Filled.Check, "Confirm")
                        }
                    }
                }
            }
        } else {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    IconButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Icon(Icons.Filled.Check, "Confirm")
                    }
                },
                dismissButton = {
                    IconButton(
                        onClick = {
                            state.selectedDateMillis = null
                            showDatePicker = false
                        }
                    ) {
                        Icon(Icons.Filled.Close, "Cancel")
                    }
                }
            ) {
                DatePicker(state = state)
            }
        }
    }
}

private fun printDate(dateMillis: Long?): String {
    val formatter = DateUtils.getDateFormatter()
    return if (dateMillis == null) {
        ""
    } else {
        formatter.format(Date(dateMillis))
    }
}