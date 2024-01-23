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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scouts.kitchenplaner.ui.navigation.Destinations
import com.scouts.kitchenplaner.ui.navigation.NavHostProjects
import com.scouts.kitchenplaner.ui.viewmodel.ProjectFrameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectLayout(
    id: Long,
    navController: NavHostController,
    viewModel: ProjectFrameViewModel = hiltViewModel()
) {
    var selectedItem by remember { mutableStateOf(0) }
    val projectNavController: NavHostController = rememberNavController()

    val sites = listOf(
        Pair("Übersicht", Destinations.ProjectsStart),
        Pair("Einkaufsliste", Destinations.ShoppingListGraph)
    )
    val backStackEntry by projectNavController.currentBackStackEntryAsState()
    val projectName by viewModel.getProjectName(id).collectAsState()
    Scaffold(topBar = {
        Column {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {
                    Text(projectName)
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
    }) {
        NavHostProjects(
            Modifier.padding(it),
            projectNavController = projectNavController,
            id = id,
            onNavigateToRecipe = { navController.navigate(Destinations.RecipeCreationGraph) })
    }
}