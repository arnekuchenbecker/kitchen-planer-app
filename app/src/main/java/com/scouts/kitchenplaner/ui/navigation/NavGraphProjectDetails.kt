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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.scouts.kitchenplaner.ui.view.ProjectLayout

private const val PROJECT_DETAILS = "details"

/**
 * The subgraph of the navigation graph concerning the project details.
 * It defines all navigation which is reachable from the projectDetailsScreen.
 *
 * @param navController The controller which performs the navigation
 */
fun NavGraphBuilder.projectsDetailsNav(navController: NavHostController) {
    navigation(
        startDestination = PROJECT_DETAILS,
        route = "${Destinations.ProjectDetailsGraph}/{${Destinations.ProjectId}}",
        arguments = listOf(navArgument(Destinations.ProjectId) { type = NavType.LongType })
    ) {
        composable(PROJECT_DETAILS) {
            ProjectLayout(
                it.arguments?.getLong(Destinations.ProjectId) ?: -1,
                navController
            )
        }
    }
}

