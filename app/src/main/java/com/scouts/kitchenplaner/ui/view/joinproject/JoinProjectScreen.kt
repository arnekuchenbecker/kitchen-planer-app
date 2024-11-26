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

package com.scouts.kitchenplaner.ui.view.joinproject

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.ui.viewmodel.JoinProjectViewModel

/**
 * Test screen for the viewmodel. Should be replaced in the relevant issue.
 *
 * @param projectID The online ID of the project to be joined
 * @param viewModel The view model to interact with the rest of the app
 */
@Composable
fun JoinProjectScreen(
    projectID: Long,
    viewModel: JoinProjectViewModel = hiltViewModel()
) {
    Column {
        Text("${viewModel.done}")
        if (!viewModel.done) {
            Button(onClick = { viewModel.joinProject(projectID) }) { Text("Click Me!") }
        }
    }
}