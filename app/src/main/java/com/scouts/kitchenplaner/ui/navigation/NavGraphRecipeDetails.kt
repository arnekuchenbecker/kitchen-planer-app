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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.scouts.kitchenplaner.ui.view.recipedetails.RecipeDetails

private const val RECIPE_DETAILS = "details"
private const val RECIPE_ID = "id"
fun NavGraphBuilder.recipeDetailsNav() {
    navigation(startDestination = RECIPE_DETAILS,
        route = "${Destinations.RecipeDetailsGraph}/{$RECIPE_ID}",
        arguments = listOf(navArgument(RECIPE_ID) { type = NavType.LongType })) {
        composable(RECIPE_DETAILS) {
            RecipeDetails(recipeID = it.arguments?.getLong(RECIPE_ID) ?: -1)
        }
    }
}