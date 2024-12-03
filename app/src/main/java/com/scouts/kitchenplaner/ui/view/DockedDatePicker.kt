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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A description text and a box next to each other. When clicking on the box a date picker appears.
 *
 * @param modifier additional modifier (not required)
 * @param dateState The state of the date picker, which saves the selected date.
 * @param displayText description text for the use of the date picker
 * @param label The alternative text if no date is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DockedDatePicker(
    modifier: Modifier = Modifier,
    dateState: DatePickerState,
    displayText: String,
    label: String
) {
    var displayDialog by remember { mutableStateOf(false) }

    DatePickerDisplay(
        modifier = modifier,
        label = label,
        displayText = displayText,
        onClick = { displayDialog = true }
    )

    if (displayDialog) {
        DatePickerDialog(
            onDismissRequest = { displayDialog = false },
            confirmButton = {
                Button(onClick = {
                    displayDialog = false
                }) {
                    Icon(Icons.Filled.Check, "Choose Date")
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}

/**
 * Alignment of a description text and a clickable box.
 *
 * @param modifier customized modifier (not required)
 * @param label alternative text which is displayed in the box
 * @param displayText description text next to the box
 * @param onClick action, what happens when clicking in the box
 */
@Composable
fun DatePickerDisplay(
    modifier: Modifier = Modifier,
    label: String,
    displayText: String,
    onClick: () -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Box(
            contentAlignment = Alignment.CenterStart, modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.4f)
                .padding(0.dp, 0.dp, 5.dp, 0.dp)
        ) {
            Text(label)
        }
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Text(displayText, modifier = Modifier.padding(5.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DockedDateRangePicker(
    datePickerState: DateRangePickerState
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("Dauer") },
            readOnly = true,
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
                .height(64.dp)
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(rememberDatePickerState())
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun previewDockedDateRangePicker() {
    DockedDateRangePicker(rememberDateRangePickerState())
}
