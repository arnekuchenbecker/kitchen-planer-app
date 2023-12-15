/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023  Arne Kuchenbecker, Antonia Heiming
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

fun NavGraphBuilder.projectsDetailsNav(navController: NavHostController) {
    navigation(
        startDestination = "$PROJECT_DETAILS/{id}",
        route = "${Destinations.ProjectDetailsGraph}/{id}",
        arguments = listOf(navArgument("id") { type = NavType.IntType })
    ) {
        composable("$PROJECT_DETAILS/{id}") {
            ProjectLayout(1)
        }
    }

}

