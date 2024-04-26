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

package com.scouts.kitchenplaner.ui.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.scouts.kitchenplaner.ui.navigation.Destinations
import com.scouts.kitchenplaner.ui.navigation.NavHostProjects
import com.scouts.kitchenplaner.ui.state.ProjectDialogValues
import com.scouts.kitchenplaner.ui.view.projectdetails.ProjectDetailsTopBar
import com.scouts.kitchenplaner.ui.view.projectdetails.ProjectSettingsSideDrawer
import com.scouts.kitchenplaner.ui.view.projectsettingsdialogs.ProjectSettingsDialogs
import com.scouts.kitchenplaner.ui.viewmodel.ProjectDetailsViewModel

@Composable
fun ProjectLayout(
    id: Long,
    navController: NavHostController,
    viewModel: ProjectDetailsViewModel = hiltViewModel()
) {
    var projectInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = null) {
        viewModel.getProject(id)
        projectInitialized = true
    }

    if (projectInitialized) {
        val project by viewModel.projectFlow.collectAsState()
        var selectedItem by remember { mutableIntStateOf(0) }

        var showSideBar by remember { mutableStateOf(false) }
        val projectNavController: NavHostController = rememberNavController()

        val sites = listOf(
            Pair("Essensplan", Destinations.ProjectsStart),
            Pair("Einkaufsliste", Destinations.ShoppingListGraph)
        )

        var displaySettingsDialog by remember { mutableStateOf(ProjectDialogValues.NONE) }

        Scaffold(
            topBar = {
                ProjectDetailsTopBar(
                    project = project,
                    showSideBar = showSideBar,
                    toggleSideBar = { showSideBar = !showSideBar },
                    selectedItem = selectedItem,
                    onSelectItem = { selectedItem = it },
                    sites = sites,
                    projectNavController = projectNavController
                )
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
                project = project,
                onNavigateToRecipe = {
                    navController.navigate(Destinations.RecipeCreationGraph)
                }
            )

            ProjectSettingsSideDrawer(
                modifier = Modifier.padding(it),
                displayDialog = { dialog ->
                    displaySettingsDialog = dialog
                    showSideBar = false
                },
                showSideBar = showSideBar,
                onDismissSideBar = {
                    showSideBar = false
                }
            )
        }

        ProjectSettingsDialogs(
            displayDialog = displaySettingsDialog,
            onNameChange = { viewModel.setProjectName(project, it) },
            onPictureChange = { viewModel.setProjectImage(project, it) },
            onDateChange = { start, end ->
                viewModel.setProjectDates(project, start, end)
            },
            onNumbersChange = {
                viewModel.setNumberChanges(project, it)
            },
            onRemovePerson = { viewModel.removeAllergenPerson(project, it) },
            onRemoveAllergen = { person, allergen -> viewModel.removeAllergenFromPerson(project, person, allergen) },
            onAddAllergenPerson = { viewModel.addAllergenPerson(project, it) },
            onDismissRequest = { displaySettingsDialog = ProjectDialogValues.NONE },
            onMealAdd = { meal, index -> viewModel.addMeal(project, meal, index) },
            onMealRemove = { viewModel.removeMeal(project, it) },
            project = project
        )
    } else {
        Text(text = "Waiting for the project to be loaded.")
    }

}