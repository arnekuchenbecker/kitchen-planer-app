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

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.scouts.kitchenplaner.ui.view.shoppinglistdetails.ShoppingList
import com.scouts.kitchenplaner.ui.view.shoppinglistoverview.ShoppingListOverview

private const val SHOPPING_OVERVIEW: String = "OverviewShopping"
private const val SHOPPING_LIST_DETAIL: String = "ShoppingList"
private const val SHOPPING_LIST_ID = "shoppingListId"
fun NavGraphBuilder.shoppingListGraph(
    navController: NavController,
    projectId: Long
) {
    navigation(
        startDestination = SHOPPING_OVERVIEW,
        route = Destinations.ShoppingListGraph
    ) {
        composable(SHOPPING_OVERVIEW) {
            ShoppingListOverview(
                projectId,
                onNavigateToShoppingList = { listID ->
                    navController.navigate("${SHOPPING_LIST_DETAIL}/$listID")
                }
            )
        }
        composable(
            "${SHOPPING_LIST_DETAIL}/{$SHOPPING_LIST_ID}",
            arguments = listOf(navArgument(SHOPPING_LIST_ID) { type = NavType.LongType })
        ) {
            ShoppingList(
                listID = (it.arguments?.getLong(SHOPPING_LIST_ID, 42) ?: -1)

            )
        }
    }
}