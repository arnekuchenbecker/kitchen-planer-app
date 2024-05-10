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
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.scouts.kitchenplaner.ui.view.createproject.CreateProject
import com.scouts.kitchenplaner.ui.view.invitetoproject.InviteToProject

private const val PROJECT_CREATION = "PROJECT_CREATION"
private const val INVITE_SCREEN = "invite"
fun NavGraphBuilder.projectCreationNav(navController: NavHostController) {

    navigation(
        startDestination = PROJECT_CREATION, route = Destinations.ProjectCreationGraph
    ) {
        composable(PROJECT_CREATION) {
            CreateProject(onNavigateToInvitePeople = { projectId ->
                navController.popBackStack()
                navController.navigate("$INVITE_SCREEN/$projectId")
            })
        }
        composable(
            "$INVITE_SCREEN/{${Destinations.ProjectId}}",
            arguments = listOf(navArgument(Destinations.ProjectId) {
                type = NavType.LongType
            })
        ) {
            val projectId = it.arguments?.getLong(Destinations.ProjectId) ?: -1
            InviteToProject (
                projectId = projectId,
                onNavigateToProject = {
                    navController.popBackStack()
                    navController.navigate("${Destinations.ProjectDetailsGraph}/$projectId")
                }
            )
        }
    }

}

