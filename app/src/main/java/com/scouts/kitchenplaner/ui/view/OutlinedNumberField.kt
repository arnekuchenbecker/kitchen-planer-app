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

package com.scouts.kitchenplaner.ui.view

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun OutlinedNumberField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    type: NumberFieldType
) {
    val pattern = when(type) {
        NumberFieldType.POSITIVE -> remember { Regex("^\\d*$") }
        NumberFieldType.INTEGER -> remember { Regex("^[+-]?\\d*$") }
        NumberFieldType.FLOAT -> remember { Regex("^[+-]?\\d*[.]?\\d*$") }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.isEmpty() || it.matches(pattern)) {
                onValueChange(it)
            } else {
                println("Didn't match regex :(")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = label,
        modifier = modifier
    )
}

enum class NumberFieldType {
    POSITIVE,
    INTEGER,
    FLOAT
}