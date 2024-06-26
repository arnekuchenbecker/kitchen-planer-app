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
import com.scouts.kitchenplaner.ui.view.recipeoverview.RecipeOverview

private const val RECIPE_OVERVIEW = "recipeOverview"
/**
 * The subgraph of the navigation graph concerning the recipe overview.
 * It defines all navigation which is reachable from the recipeOverviewScreen.
 *
 * @param navController The controller which performs the navigation
 */
fun NavGraphBuilder.recipesNav(navController: NavHostController) {
    navigation(startDestination = RECIPE_OVERVIEW, route = Destinations.RecipesGraph) {
        composable(RECIPE_OVERVIEW) {
            RecipeOverview(
                onNavigationCreateRecipe = {
                    navController.navigate(
                        Destinations.RecipeCreationGraph
                    )
                },
                onNavigateToDetailedRecipe = { recipeId -> navController.navigate("${Destinations.RecipeDetailsGraph}/$recipeId") })
        }
        recipeCreationNav(navController = navController)
        recipeDetailsNav()
    }
}
