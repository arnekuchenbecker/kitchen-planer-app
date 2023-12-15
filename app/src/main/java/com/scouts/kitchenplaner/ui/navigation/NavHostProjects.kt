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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.scouts.kitchenplaner.ui.view.projectDetails.ProjectDetails

@Composable
fun NavHostProjects(
    modifier: Modifier = Modifier,
    projectNavController: NavHostController = rememberNavController(),
    startDestination: String = "Screen/{id}"
) {
    NavHost(
        modifier = modifier,
        navController = projectNavController,
        startDestination = startDestination
    ) {
        shoppinListGraph(navController = projectNavController)
        composable(
            "Screen/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { argu -> ProjectDetails(projectID = argu.arguments?.getInt("id") ?: -1) }
    }
}