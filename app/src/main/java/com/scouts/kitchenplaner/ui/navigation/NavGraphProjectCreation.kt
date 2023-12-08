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

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scouts.kitchenplaner.ui.view.inviteToProject.InviteToProject
import com.scouts.kitchenplaner.ui.view.createProject.ProjectCreation

private const val PROJECT_CREATION = "PROJECT_CREATION"
private const val INVITE_SCREEN = "invite"
fun NavGraphBuilder.projectCreationNav(navController: NavController) {

    navigation(
        startDestination = PROJECT_CREATION,
        route = Destinations.ProjectCreationGraph
    ) {
        composable(PROJECT_CREATION) {
            ProjectCreation(onNavigateToInvitePeople = { navController.navigate(INVITE_SCREEN) })
        }
        composable(INVITE_SCREEN) { InviteToProject() }
    }
}

