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
import androidx.navigation.navArgument
import com.scouts.kitchenplaner.ui.view.projectDetails.ProjectDetails
import com.scouts.kitchenplaner.ui.view.recipeForProject.RecipeForProjectScreen

private const val RECIPE_TO_COOK = "recipeToCook"

@Composable
fun NavHostProjects(
    modifier: Modifier = Modifier,
    projectNavController: NavHostController,
    id: Long = 0L,
    onNavigateToRecipe: () -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = projectNavController,
        startDestination = "${Destinations.ProjectsStart}/$id",
    ) {
        shoppingListGraph(navController = projectNavController)
        composable(
            "${Destinations.ProjectsStart}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { projectId ->
            var project = projectId.arguments?.getLong("id")
            println("in project details Overview with $project")
            if (project == 0L) {
                project = id
            }
            ProjectDetails(
                projectID = project!!,
                onNavigateToRecipeToCook = { recipeID -> projectNavController.navigate("${RECIPE_TO_COOK}/$recipeID") },
                onNavigateToRecipeCreation = onNavigateToRecipe
            )


        }
        composable(
            "${RECIPE_TO_COOK}/{recipeID}",
            arguments = listOf(navArgument("recipeID") { type = NavType.LongType })
        ) {
            RecipeForProjectScreen(
                projectNavController.currentBackStackEntry?.arguments?.getLong("recipeID") ?: -1
            )
        }
    }
}
