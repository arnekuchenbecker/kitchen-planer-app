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

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.scouts.kitchenplaner.ui.state.CreateProjectInputState
import com.scouts.kitchenplaner.ui.state.StateProjectValidator
import com.scouts.kitchenplaner.ui.theme.KitchenPlanerTheme
import com.scouts.kitchenplaner.ui.view.createproject.CreateProjectContent

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ProjectCreationPreview() {
    val state = CreateProjectInputState()
    val validator = StateProjectValidator(state)

    KitchenPlanerTheme(dynamicColor = false) {
        CreateProjectContent(
            onProjectCreate = {
                validator.validateAll()
            },
            state = state,
            validator = validator
        )
    }
}
