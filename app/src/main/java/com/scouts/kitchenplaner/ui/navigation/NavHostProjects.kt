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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.ui.view.projectdetails.ProjectDetails
import com.scouts.kitchenplaner.ui.view.recipeforproject.RecipeForProjectScreen
import java.util.Date

private const val RECIPE_ID = "recipeID"
private const val DATE = "date"
private const val MEAL = "meal"
private const val RECIPE_TO_COOK = "recipeToCook"

/**
 * The navHost which is in charge of the navigation through the given project.
 * The navigation starts at the StartDestination.
 *
 * @param modifier A customized modifier which is used in for the whole layout of the project
 * @param projectNavController The controller which performs the navigation
 * @param project The project within which the navigation takes places
 * @param onNavigateToRecipeCreation Callback function to create a new recipe
 */
@Composable
fun NavHostProjects(
    modifier: Modifier = Modifier,
    projectNavController: NavHostController,
    project: Project,
    onNavigateToRecipeCreation: () -> Unit,
    onNavigateToRecipeDetails: (Long) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = projectNavController,
        startDestination = Destinations.ProjectsStart,
    ) {
        composable(
            Destinations.ProjectsStart
        ) {
            ProjectDetails(
                project = project,
                onNavigateToRecipeToCook = { recipeID, mealSlot ->
                    projectNavController.navigate("${RECIPE_TO_COOK}/$recipeID/${mealSlot.date.time}/${mealSlot.meal}")
                },
                onNavigateToRecipeCreation = onNavigateToRecipeCreation
            )
        }
        composable(
            "${RECIPE_TO_COOK}/{$RECIPE_ID}/{$DATE}/{$MEAL}",
            arguments = listOf(
                navArgument(RECIPE_ID) { type = NavType.LongType },
                navArgument(DATE) { type = NavType.LongType },
                navArgument(MEAL) { type = NavType.StringType }
            )
        ) {
            val mealSlot = MealSlot(
                Date(projectNavController.currentBackStackEntry?.arguments?.getLong(DATE) ?: 0),
                projectNavController.currentBackStackEntry?.arguments?.getString(MEAL) ?: ""
            )

            RecipeForProjectScreen(
                project = project,
                mealSlot = mealSlot,
                recipeID = projectNavController.currentBackStackEntry?.arguments?.getLong(RECIPE_ID) ?: -1,
                onNavigateToRecipeDetails = onNavigateToRecipeDetails,
                onNavigateToAlternative = {
                    projectNavController.navigate("$RECIPE_TO_COOK/$it/${mealSlot.date.time}/${mealSlot.meal}") {
                        popUpTo(Destinations.ProjectsStart) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        shoppingListGraph(navController = projectNavController, project = project)
    }
}
