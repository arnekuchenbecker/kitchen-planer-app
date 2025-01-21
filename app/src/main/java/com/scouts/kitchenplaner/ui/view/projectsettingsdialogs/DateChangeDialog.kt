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

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import com.scouts.kitchenplaner.ui.view.ModalDateRangePicker
import java.util.Date

/**
 * Dialog for changing the start and end date of a project
 *
 * @param onDismissRequest Callback function to close the dialog
 * @param onDateChange Callback function to change start and end date of a project to new values
 * @param startDate The current start date
 * @param endDate The current end date
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateChangeDialog(
    onDismissRequest: () -> Unit,
    onDateChange: (Date, Date) -> Unit,
    startDate: Date,
    endDate: Date
) {
    val dates = rememberDateRangePickerState(startDate.time, endDate.time)
    SettingDialog(
        onDismissRequest = onDismissRequest,
        title = "Daten ändern",
        onConfirm = {
            onDateChange(
                Date(dates.selectedStartDateMillis ?: 0),
                Date(dates.selectedEndDateMillis ?: 0)
            )
        }
    ) {
        ModalDateRangePicker(
            state = dates
        )
    }
}