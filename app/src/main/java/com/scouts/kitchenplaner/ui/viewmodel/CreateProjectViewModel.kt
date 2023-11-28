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

package com.scouts.kitchenplaner.ui.viewmodel

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CreateProjectViewModel : ViewModel() {
    val nameText: MutableState<String> = mutableStateOf("")
    @OptIn(ExperimentalMaterial3Api::class)
    val projectDates: MutableState<DatePickerState> = mutableStateOf(
        DatePickerState(null, null, IntRange(2000, 2100), DisplayMode.Picker)
    )

    val displayStartPicker: MutableState<Boolean> = mutableStateOf(false)

    fun onProjectCreate() {
        println("Creating Project with name ${nameText.value}")
    }
}