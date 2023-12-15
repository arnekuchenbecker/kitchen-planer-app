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

package com.scouts.kitchenplaner.ui.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DockedDatePicker(modifier: Modifier = Modifier, dateState: DatePickerState, displayText: String, label: String) {
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

@Composable
fun DatePickerDisplay(modifier: Modifier = Modifier, label: String, displayText: String, onClick: () -> Unit) {
    Row (modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.4f)
            .padding(0.dp, 0.dp, 5.dp, 0.dp)) {
            Text(label)
        }
        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .border(2.dp, MaterialTheme.colorScheme.primary)
            .fillMaxHeight()
            .fillMaxWidth()
            .clickable(onClick = onClick)) {
            Text(displayText, modifier = Modifier.padding(5.dp))
        }
    }
}