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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.scouts.kitchenplaner.ui.navigation.Destinations
import com.scouts.kitchenplaner.ui.navigation.NavHostProjects
import com.scouts.kitchenplaner.ui.state.ProjectDialogValues
import com.scouts.kitchenplaner.ui.state.ProjectDialogsState
import com.scouts.kitchenplaner.ui.viewmodel.ProjectFrameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectLayout(
    id: Long,
    navController: NavHostController,
    viewModel: ProjectFrameViewModel = hiltViewModel()
) {
    var selectedItem by remember { mutableStateOf(0) }

    val dialogState = remember {
        ProjectDialogsState(
            onNameChange = { viewModel.setProjectName(id, it) },
            onPictureChange = {}
        )
    }
    var showSideBar by remember { mutableStateOf(false) }
    val projectNavController: NavHostController = rememberNavController()

    val sites = listOf(
        Pair("Übersicht", Destinations.ProjectsStart),
        Pair("Einkaufsliste", Destinations.ShoppingListGraph)
    )
    val backStackEntry by projectNavController.currentBackStackEntryAsState()
    val project by viewModel.getProjectStub(id).collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = project.imageUri,
                                contentDescription = "Project image",
                                modifier = Modifier
                                    .width(45.dp)
                                    .aspectRatio(1.0f)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Text(text = project.name, modifier = Modifier.padding(start = 10.dp))
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                showSideBar = !showSideBar
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        ) {
                            Crossfade(targetState = showSideBar, label = "Icon Fade") { showClose ->
                                if (showClose) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hide Sidebar",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Expand Sidebar",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                )
                SecondaryTabRow(selectedTabIndex = selectedItem) {
                    sites.forEachIndexed { index, site ->
                        if (backStackEntry?.destination?.hierarchy?.any { it.route == "${site.second}/{${Destinations.ProjectId}}" } == true) {
                            selectedItem = index;
                        }
                        Tab(selected = selectedItem == index, onClick = {
                            projectNavController.navigate("${site.second}/$id") {
                                launchSingleTop = true
                            }
                        }, text = { Text(site.first) })

                    }
                }
            }
        }
    ) {
        NavHostProjects(
            modifier = Modifier
                .padding(it)
                .pointerInput(Unit) {
                    detectTapGestures {
                        showSideBar = false
                    }
                },
            projectNavController = projectNavController,
            id = id,
            onNavigateToRecipe = { navController.navigate(Destinations.RecipeCreationGraph) }
        )
        SideDrawer(
            expand = showSideBar,
            modifier = Modifier.padding(it)
        ) {
            Text(
                text = "Einstellungen",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = 8.em,
                modifier = Modifier.padding(20.dp).align(Alignment.CenterHorizontally)
            )
            HorizontalDivider()
            SideDrawerItem(
                content = {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit project name", modifier = Modifier.padding(end = 15.dp))
                    Text(text = "Namen ändern")
                },
                onClick = {
                    dialogState.displayDialog = ProjectDialogValues.NAME_CHANGE
                    showSideBar = false
                }
            )
            SideDrawerItem(
                content = {
                    Icon(imageVector = Icons.Filled.ImageSearch, contentDescription = "Edit project image", modifier = Modifier.padding(end = 15.dp))
                    Text(text = "Projekt-Bild ändern")
                },
                onClick = {
                    dialogState.displayDialog = ProjectDialogValues.IMAGE_CHANGE
                    showSideBar = false
                }
            )
            SideDrawerItem(
                content = {
                    Icon(imageVector = Icons.Filled.EditCalendar, contentDescription = "Edit start / end date", modifier = Modifier.padding(end = 15.dp))
                    Text(text = "Start-/Enddatum")
                },
                onClick = {
                    dialogState.displayDialog = ProjectDialogValues.DATE_CHANGE
                    showSideBar = false
                }
            )
            SideDrawerItem(
                content = {
                    Icon(imageVector = Icons.Filled.SwapHoriz, contentDescription = "Edit arrivals and departures", modifier = Modifier.padding(end = 15.dp))
                    Text(text = "Ankunft / Abfahrt")
                },
                onClick = {
                    dialogState.displayDialog = ProjectDialogValues.NUMBER_CHANGE
                    showSideBar = false
                }
            )
            SideDrawerItem(
                content = {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "Invite other people", modifier = Modifier.padding(end = 15.dp))
                    Text(text = "Teilen")
                },
                onClick = {
                    dialogState.displayDialog = ProjectDialogValues.NUMBER_CHANGE
                    showSideBar = false
                }
            )
        }
    }

    ProjectSettingsDialogs(
        state = dialogState
    )
}