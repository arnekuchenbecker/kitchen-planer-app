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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HideImage
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
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
    var archive by remember { mutableStateOf(false) }


    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            title = {
                Text("Meine Projekte")
            },
            actions = {
                Button(
                    onClick = {
                        archive = !archive
                    }
                ) {
                    if (archive) {
                        Icon(imageVector = Icons.Filled.Cancel, contentDescription = "abbrechen")
                    } else {
                        Icon(imageVector = Icons.Filled.Archive, contentDescription = "archivieren")
                    }
                }
            }
        )
    }, floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = {
                if (archive) {
                    archive = !archive;
                    //TODO delete projects from database

                } else {
                    onNavigateToCreateProject()
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            text = {
                if (!archive) {
                    Text("Neues Projekt")
                }
            },
            icon = {
                if (archive) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "projekte archivieren"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "neues Projekt hinzufügen"
                    )
                }

            }
        )

    }) {
        val projects by viewModel.projects.collectAsState(initial = listOf())

        println(projects.size)
        Box(modifier = Modifier.padding(it)) {
            LazyColumnWrapper(
                modifier = Modifier.padding(10.dp),
                content = projects,
                DisplayContent = { project, _ ->
                    Box(
                        modifier = Modifier
                            .clickable {
                                onNavigateToDetailedProject(
                                    project.id
                                )
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
                                .padding(start = 5.dp)
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
                                println("ProjectURI ${project.imageUri} for ${project.name}")
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
                        }

                    }
                }, DisplayEmpty = {
                    Text("${projects.size}")
                    Text(
                        text = "Keine Projekte"
                    )
                })

        }

    }
}



