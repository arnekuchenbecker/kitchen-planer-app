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

package com.scouts.kitchenplaner.ui.view.projectDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ProjectDetails(
    projectID: Long,
    onNavigateToRecipeToCook: (Long) -> Unit,
    onNavigateToRecipeCreation: () -> Unit
) {
    Column {

        Text(text = "This is the screen, where all information to one specific project are displayed displayed")
        Text(text = "The projectID is $projectID", color = Color.Red)
        Text(text = "available Links to other sides are: ")
        var listID by remember { mutableStateOf(1f) }

        Row {
            Text(text = "Recipe To Cook")
            Slider(
                modifier = Modifier.fillMaxWidth(0.3f),
                value = listID,
                onValueChange = { listID = it },
                valueRange = 1f..20f,
                steps = 5
            )
            Button(onClick = { onNavigateToRecipeToCook(listID.toLong()) }) {}
        }
        Row {
            Text(text = "Create recipe screen")
            Button(onClick = onNavigateToRecipeCreation) {}
        }

    }
}