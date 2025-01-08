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

package com.scouts.kitchenplaner.ui.view.previews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.ui.theme.KitchenPlanerTheme
import com.scouts.kitchenplaner.ui.view.ModalDateRangePicker
import com.scouts.kitchenplaner.ui.view.ModalMealSlotPicker

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewModalDateRangePicker() {
    KitchenPlanerTheme(dynamicColor = false) {
        ModalDateRangePicker(
            modifier = Modifier.fillMaxWidth(),
            state = rememberDateRangePickerState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewMealSlotPicker() {
    val meals = remember { listOf("Frühstück", "Mittagessen", "Abendessen") }
    var selectedMeal by remember { mutableStateOf<String?>(null) }

    KitchenPlanerTheme(dynamicColor = false) {
        ModalMealSlotPicker(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            meals = meals,
            state = rememberDatePickerState(),
            selectedMeal = selectedMeal ?: "Mahlzeit auswählen",
            onSelectMeal = { selectedMeal = it }
        )
    }
}