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

package com.scouts.kitchenplaner.ui.view.createproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AllergenAdder(onAdd: (String, String, Boolean) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var name by remember { mutableStateOf("") }
        var allergen by remember { mutableStateOf("") }
        var traces by remember { mutableStateOf(false) }

        AllergenInputs(
            onNameChange = { name = it },
            onAllergenChange = { allergen = it },
            onTracesChange = { traces = it },
            name = name,
            allergen = allergen,
            traces = traces
        )

        Button(
            onClick = {
                if (name != "" && allergen != "") {
                    onAdd(name, allergen, traces)
                    name = ""
                    allergen = ""
                    traces = false
                }
            },
            modifier = Modifier.padding(start = 10.dp),
            contentPadding = PaddingValues(5.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add allergen for person")
        }
    }
}

@Composable
fun AllergenInputs (
    onNameChange: (String) -> Unit,
    onAllergenChange: (String) -> Unit,
    onTracesChange: (Boolean) -> Unit,
    name: String,
    allergen: String,
    traces: Boolean) {
    Column {
        TextField(
            modifier = Modifier.fillMaxWidth(0.8f),
            label = { Text("Name") },
            value = name,
            onValueChange = onNameChange,
            singleLine = true
        )

        TextField(
            modifier = Modifier.fillMaxWidth(0.8f),
            label = { Text("Allergen") },
            value = allergen,
            onValueChange = onAllergenChange,
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Spuren")

            Checkbox(checked = traces, onCheckedChange = onTracesChange)
        }
    }
}