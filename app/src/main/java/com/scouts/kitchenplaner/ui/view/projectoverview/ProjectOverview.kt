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

package com.scouts.kitchenplaner.ui.view.projectoverview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.viewmodel.ProjectSelectionViewModel

@Composable

fun ProjectOverview(
    onNavigateToDetailedProject: (Long) -> Unit,
    onNavigateToCreateProject: () -> Unit,
    viewModel: ProjectSelectionViewModel = hiltViewModel()
) {
    var projectId by remember { mutableStateOf(0f) }

    Scaffold(floatingActionButton = {

        ExtendedFloatingActionButton(
            onClick = onNavigateToCreateProject,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            text = { Text("Neues Projekt") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "neues Projekt hinzufügen"
                )
            }
        )

    }) {
        val projects by viewModel.projects.collectAsState(initial = listOf())

        println(projects.size)
        LazyColumnWrapper(
            modifier = Modifier.padding(it),
            content = projects,
            DisplayContent = { project, _ ->
                Box(
                    modifier = Modifier
                        .clickable {
                            onNavigateToDetailedProject(
                                project.id
                            )
                        }.fillMaxWidth().background(color = MaterialTheme.colorScheme.secondaryContainer),


                ) {
                    Row {
                        if (project.imageUri.path.isNullOrEmpty()) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Projektplatzhalter"
                            )
                        } else {
                            AsyncImage(
                                model = project.imageUri.path,
                                contentDescription = "Project Image",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(text = project.name,  color= MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }, DisplayEmpty = {
                Text("${projects.size}")
                Text(
                    text = "Keine Projekte"
                )
            })
        /*Column(modifier = Modifier.padding(it)) {
            Text(text = "This is the project overview, where all own projects are displayed")
            Text(text = "available Links to other sides are: ")
            Row {
                Text("ProjectDetails")
                Slider(
                    modifier = Modifier.fillMaxWidth(0.3f),
                    value = projectId,
                    onValueChange = { projectId = it },
                    valueRange = 1f..5f,
                    steps = 5
                )
                Button(onClick = { onNavigateToDetailedProject(projectId.toLong()) }) {}


            }
            Row {
                Text("ProjectCreation")
                Button(onClick = onNavigateToCreateProject) {}
            } */
    }
}



