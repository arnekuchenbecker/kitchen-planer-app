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

package com.scouts.kitchenplaner.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scouts.kitchenplaner.ui.view.projectoverview.ProjectOverview

private const val PROJECT_OVERVIEW = "projectOverview"

fun NavGraphBuilder.projectsNav(
    navController: NavHostController,
) {
    navigation(startDestination = PROJECT_OVERVIEW, route = Destinations.ProjectsGraph) {
        composable(PROJECT_OVERVIEW) {
            ProjectOverview(
                onNavigateToDetailedProject = { projectID ->
                    navController.navigate(
                        Destinations.ProjectDetailsGraph + "/$projectID"
                    )
                },
                onNavigateToCreateProject = { navController.navigate(Destinations.ProjectCreationGraph) })
        }

        projectsDetailsNav(navController = navController)
        projectCreationNav(navController = navController)

    }
}