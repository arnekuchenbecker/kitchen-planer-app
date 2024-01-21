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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.viewmodel.ProjectSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ProjectOverview(
    onNavigateToDetailedProject: (Long) -> Unit,
    onNavigateToCreateProject: () -> Unit,
    viewModel: ProjectSelectionViewModel = hiltViewModel()
) {
    val projects by viewModel.projectSelected.collectAsState(initial = listOf())

    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ), title = {
            Text("Meine Projekte")
        }, actions = {
            Button(onClick = {
                viewModel.archive = !viewModel.archive
                viewModel.clearSelection()
            }) {
                if (viewModel.archive) {
                    Icon(
                        imageVector = Icons.Filled.Cancel, contentDescription = "abbrechen"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "projekte löschen"
                    )
                }
            }
        })
    }, floatingActionButton = {
        if (!viewModel.archive) {
            ExtendedFloatingActionButton(onClick = onNavigateToCreateProject,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                text = { Text("Neues Projekt") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "neues Projekt hinzufügen"
                    )
                })
        } else {
            ExtendedFloatingActionButton(onClick = { viewModel.showArchiveDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                text = { Text("Projekte löschen") },
                icon = {

                    Icon(
                        imageVector = Icons.Filled.Check, contentDescription = "projekte löschen"
                    )
                })
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            LazyColumnWrapper(modifier = Modifier.padding(10.dp),
                content = projects,
                DisplayContent = { (project, selected), _ ->
                    ProjectField(
                        project = project,
                        selected = selected,
                        onNavigateToDetailedProject = onNavigateToDetailedProject,
                        toggleSelection = viewModel::toggleSelection,
                        archive = viewModel.archive
                    )
                }, DisplayLast = { (project, selected), _ ->
                    ProjectField(
                        project = project,
                        selected = selected,
                        onNavigateToDetailedProject = onNavigateToDetailedProject,
                        toggleSelection = viewModel::toggleSelection,
                        archive = viewModel.archive
                    )
                    //To allow scrolling stuff from behind the FAB
                    Spacer(modifier = Modifier.height(75.dp))
                },
                DisplayEmpty = {
                    Text(
                        text = "Keine Projekte"
                    )
                })

        }
        if (viewModel.showArchiveDialog) {
            ArchiveDialog(projects, onCloseDialog = {
                viewModel.showArchiveDialog = false
                viewModel.archive = false
                viewModel.clearSelection()
            })
        }
    }
}
