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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.ui.viewmodel.StartScreenViewModel

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetailedProject: (Long) -> Unit,
    onNavigateToProjectCreation: () -> Unit,
    onNavigateToCreateRecipe: () -> Unit,
    onNavigateToRecipeDetail: (Long) -> Unit,
    viewModel: StartScreenViewModel = hiltViewModel()
) {
    val projects by viewModel.latestProjects.collectAsState(initial = listOf())
    val recipes by viewModel.latestRecipes.collectAsState(initial = listOf())
    Scaffold(topBar = {
        HeaderWithButton(
            title = "Übersicht",
            buttonClick = { /*Logout*/ }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout button"
            )
        }
    })
    {
        Column(modifier = modifier.padding(it)) {
            ScreenHeader(
                buttonClick = onNavigateToProjectCreation,
                buttonText = "Neues Projekt",
                fieldText = "Meine Projekte"
            )
            LazyColumnWrapper(content = projects, DisplayContent = { project, _ ->
                OverviewField(
                    onClick = { onNavigateToDetailedProject(project.id) },
                    imageUri = project.imageUri,
                    imageDescription = "Project Picture for project ${project.name}",
                    text = project.name
                )
            }, DisplayEmpty = {
                Text("Noch keine Projekte ")
            })

            ScreenHeader(
                buttonClick = onNavigateToCreateRecipe,
                buttonText = "Neues Rezept",
                fieldText = "Meine Rezepte"
            )

            LazyColumnWrapper(content = recipes, DisplayContent = { recipe, _ ->
                OverviewField(
                    onClick = { onNavigateToRecipeDetail(recipe.id) },
                    imageUri = recipe.imageURI,
                    imageDescription = "Recipe Picture for project ${recipe.name}",
                    text = recipe.name
                )
            }, DisplayEmpty = {
                Text(" Noch keine Rezepte angeschaut")
            })
        }
    }
}

@Preview
@Composable
fun startScreenPreview() {
}

@Composable
fun ScreenHeader(buttonClick: () -> Unit, buttonText: String, fieldText: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
            .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = fieldText,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(2f, true),
            textAlign = TextAlign.Center,
            fontSize = TextUnit(5f, TextUnitType.Em),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Button(
            onClick = buttonClick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add new Projekt",
            )
            Text(buttonText)
        }
    }
}