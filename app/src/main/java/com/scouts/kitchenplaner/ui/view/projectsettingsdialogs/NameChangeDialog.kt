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

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NameChangeDialog(
    onDismissRequest: () -> Unit,
    onNameChange: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    SettingDialog(
        onDismissRequest = onDismissRequest,
        title = "Namen ändern",
        onConfirm = { onNameChange(text) }
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Projekt Name") },
            modifier = Modifier.padding(bottom = 20.dp),
            singleLine = true
        )
    }
}