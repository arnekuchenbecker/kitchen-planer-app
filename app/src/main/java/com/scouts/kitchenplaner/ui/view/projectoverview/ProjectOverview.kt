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

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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
        ExtendedFloatingActionButton(
            onClick = {
                if (!viewModel.archive) {
                    onNavigateToCreateProject()
                } else {
                    viewModel.showArchiveDialog = true
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            text = {
                if (!viewModel.archive) {
                    Text("Neues Projekt")
                } else {
                    Text("Projekte löschen")
                }
            },
            icon = {
                if (!viewModel.archive) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "neues Projekt hinzufügen"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Check, contentDescription = "projekte löschen"
                    )
                }

            })

    }) {
        fun closeDialog() {
            viewModel.showArchiveDialog = false
            viewModel.archive = false
            viewModel.clearSelection()
        }
        Box(modifier = Modifier.padding(it)) {
            LazyColumnWrapper(modifier = Modifier.padding(10.dp),
                content = projects,
                DisplayContent = { (project, selected), _ ->
                    Box(modifier = Modifier
                        .clickable {
                            if (!viewModel.archive) {
                                onNavigateToDetailedProject(
                                    project.id
                                )
                            } else {
                                viewModel.toggleSelection(
                                    projectId = project.id,
                                    selected = !selected
                                )
                                println("Project ${project.id} is selected = $selected")
                            }
                        }
                        .fillMaxWidth()
                        .padding(5.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .height(75.dp)

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 5.dp, end = 15.dp)
                        ) {
                            if (project.imageUri == Uri.EMPTY) {
                                Icon(
                                    modifier = Modifier
                                        .fillMaxHeight(0.9f)
                                        .aspectRatio(1.0f)
                                        .padding(start = 5.dp),
                                    imageVector = Icons.Filled.HideImage,
                                    contentDescription = "Projektplatzhalter"
                                )
                            } else {
                                AsyncImage(
                                    model = project.imageUri,
                                    contentDescription = "Project Image",
                                    modifier = Modifier
                                        .fillMaxHeight(0.85f)
                                        .aspectRatio(1.0f)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                            Text(
                                modifier = Modifier.padding(10.dp),
                                text = project.name,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            println("selected: $selected")
                            if (viewModel.archive && selected) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "zu löschendes projekt",
                                    tint= MaterialTheme.colorScheme.error,
                                    modifier= Modifier
                                        .fillMaxHeight(0.7f)
                                        .aspectRatio(1.0f)

                                )
                            }
                        }

                    }
                },
                DisplayEmpty = {
                    Text("${projects.size}")
                    Text(
                        text = "Keine Projekte"
                    )
                })

        }

        if (viewModel.showArchiveDialog) {
            Dialog(onDismissRequest = { viewModel.showArchiveDialog = false }

            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("geteilte Projekte werden so vom Gerät entfernt. \nEs ist möglich diese wieder zu laden. \nNicht geteilte Projekte werden gelöscht und können nicht wieder hergestellt werden.")
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { /*TODO delete selected projects*/
                                projects.forEach { (project, selected) ->
                                    if (selected) {
                                        println(project.name)
                                    }
                                }
                                closeDialog()
                            }) { Text("OK") }
                            Button(onClick = { closeDialog() }
                            ) { Text("Abbrechen") }
                        }
                    }
                }
            }
        }
    }
}



