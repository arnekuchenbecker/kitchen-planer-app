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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scouts.kitchenplaner.ui.view.StartScreen

/**
 * Defines the Nav host which performs the navigation and provides the nav controller.
 * It starts the navigation through the app at the given startDestionation
 *
 * @param modifier Custom modifier which are used in the start screen
 * @param navController The used navController for the navigation
 * @param startDestination The destination which should be reached when the controller starts.
 */
@Composable
fun NavHostGeneral(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.Home
) {
    NavHost(
        modifier = modifier, navController = navController, startDestination = startDestination
    ) {
        composable(Destinations.Home) {
            StartScreen(
                onNavigateToDetailedProject = { projectID ->
                    navController.navigate(Destinations.ProjectDetailsGraph + "/$projectID")
                },
                onNavigateToProjectCreation = {
                    navController.navigate(Destinations.ProjectCreationGraph)
                },
                onNavigateToCreateRecipe = {
                    navController.navigate(Destinations.RecipeCreationGraph)
                },
                onNavigateToRecipeDetail = { recipeID ->
                    navController.navigate("${Destinations.RecipeDetailsGraph}/$recipeID")
                }
            )

        }
        projectsNav(navController = navController)
        recipesNav(navController = navController)

    }
}