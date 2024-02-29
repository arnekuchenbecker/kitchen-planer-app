/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming
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

import androidx.compose.foundation.layout.height
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.toDateString
import com.scouts.kitchenplaner.ui.view.DockedDatePicker
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateChangeDialog(
    onDismissRequest: () -> Unit,
    onDateChange: (Date, Date) -> Unit,
    startDate: Date,
    endDate: Date
) {
    val startState = remember { DatePickerState(Locale.GERMAN, initialSelectedDateMillis = startDate.time) }
    val endState = remember { DatePickerState(Locale.GERMAN, initialSelectedDateMillis = endDate.time) }
    SettingDialog(
        onDismissRequest = onDismissRequest,
        title = "Daten ändern",
        onConfirm = { onDateChange(Date(startState.selectedDateMillis ?: 0), Date(endState.selectedDateMillis ?: 0)) }
    ) {
        DockedDatePicker(
            dateState = startState,
            displayText = startState.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt",
            label = "Start-Datum",
            modifier = Modifier.height(70.dp)
        )
        DockedDatePicker(
            dateState = endState,
            displayText = endState.selectedDateMillis?.toDateString() ?: "Kein Datum ausgewählt",
            label = "End-Datum",
            modifier = Modifier.height(70.dp)
        )
    }
}