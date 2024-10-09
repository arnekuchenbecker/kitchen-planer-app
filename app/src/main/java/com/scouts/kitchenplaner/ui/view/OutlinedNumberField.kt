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

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * A number field which supports adding integer, non negative integers and float values
 *
 * @param modifier additional modifier (not required)
 * @param value  current displayed value at the field
 * @param onValueChange action, what happens when a new number (String) is entered in the field
 * @param label Content description, what should be entered in this text field
 * @param type Type of valid inputs
 */
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
        modifier = modifier,
        singleLine = true
    )
}

/**
 * Specification of valid inputs for the number field
 */
enum class NumberFieldType {
    /**
     * represents non negative integers
     */
    POSITIVE,

    /**
     * represents an integer
     */
    INTEGER,

    /**
     * represents a float
     */
    FLOAT
}