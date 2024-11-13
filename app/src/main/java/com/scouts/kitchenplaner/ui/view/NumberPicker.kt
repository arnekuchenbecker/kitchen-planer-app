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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A number text field for integers which provides increasing and decreasing buttons.
 *
 * @param value The current value
 * @param modifier additional modifiers
 * @param onValueChange Callback what happens when the value changes
 * @param label Description what the field is used for
 */
@Composable
fun NumberPicker(
    value: String = "0", modifier: Modifier,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
) {
    var currentNumber by remember { mutableIntStateOf(value.toInt()) }

    OutlinedNumberField(
        modifier = modifier.padding(3.dp),
        value = currentNumber.toString(),
        onValueChange = { currentNumber = it.toInt(); onValueChange(it) },
        leadingIcon = {
            Icon(Icons.Outlined.ArrowUpward,
                contentDescription = null,
                modifier = Modifier.clickable { currentNumber++; onValueChange(currentNumber.toString()) })
        },
        trailingIcon = {
            Icon(Icons.Outlined.ArrowDownward,
                contentDescription = null,
                modifier = Modifier.clickable { currentNumber--; onValueChange(currentNumber.toString()) })
        },
        type = NumberFieldType.POSITIVE,
        label = label,
        textAlign = TextAlign.Center
    )
}
