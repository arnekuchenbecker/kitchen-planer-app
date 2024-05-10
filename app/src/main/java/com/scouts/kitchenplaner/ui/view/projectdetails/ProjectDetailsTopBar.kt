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

package com.scouts.kitchenplaner.ui.view.projectdetails

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.scouts.kitchenplaner.model.entities.Project

/**
 * Top bar for the project details screen
 *
 * @param project The project being displayed
 * @param showSideBar If the side bar for the project settings should be shown
 * @param toggleSideBar Callback function to toggle side bar visibility
 * @param selectedItem Index for navigating the project details view
 * @param onSelectItem Callback function for navigation
 * @param sites List of destinations in the project details view
 * @param projectNavController The NavController to be used for navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsTopBar(
    project: Project,
    showSideBar: Boolean,
    toggleSideBar: () -> Unit,
    selectedItem: Int,
    onSelectItem: (Int) -> Unit,
    sites: List<Pair<String, String>>,
    projectNavController: NavHostController
) {
    val backStackEntry by projectNavController.currentBackStackEntryAsState()

    Column {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = project.projectImage,
                        contentDescription = "Project image",
                        modifier = Modifier
                            .width(45.dp)
                            .aspectRatio(1.0f)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = project.name,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = toggleSideBar,
                    colors = IconButtonDefaults.filledIconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    Crossfade(
                        targetState = showSideBar,
                        label = "Icon Fade"
                    ) { showClose ->
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
                if (backStackEntry?.destination?.hierarchy?.any { it.route == site.second } == true) {
                    onSelectItem(index)
                }
                Tab(selected = selectedItem == index, onClick = {
                    projectNavController.navigate(site.second) {
                        launchSingleTop = true
                    }
                }, text = { Text(site.first) })

            }
        }
    }
}