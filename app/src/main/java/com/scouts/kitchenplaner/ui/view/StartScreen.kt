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

package com.scouts.kitchenplaner.ui.view

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

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetailedProject: (Long) -> Unit,
    onNavigateToProjectCreation: () -> Unit,
    onNavigateToCreateRecipe: () -> Unit,
    onNavigateToRecipeDetail: (Long) -> Unit
) {
    var projectId by remember { mutableStateOf(0f) }
    var recipeID by remember { mutableStateOf(0f) }
    Column(modifier = modifier) {
        Text(text = "This is the start screen, where recipes etc are displayed")
        Text(text = "available Links to other sides are: ")
        Row {
            Text("ProjectDetails")
            Slider(
                modifier = Modifier.fillMaxWidth(0.3f),
                value = projectId,
                onValueChange = { projectId = it },
                valueRange = 1f..10f,
                steps = 1
            )
            Button(onClick = { onNavigateToDetailedProject(projectId.toLong()) }) {}

        }
        Row {
            Text("ProjectCreation")
            Button(onClick = onNavigateToProjectCreation) {}
        }
        Row {
            Text("RecipeCreation")
            Button(onClick = onNavigateToCreateRecipe) {}
        }
        Row {
            Text("RecipeDetails")
            Slider(
                modifier = Modifier.fillMaxWidth(0.3f),
                value = recipeID,
                onValueChange = { recipeID = it },
                valueRange = 1f..15f,
                steps = 15
            )
            Button(onClick = { onNavigateToRecipeDetail(recipeID.toLong()) }) {

            }
        }
    }
}