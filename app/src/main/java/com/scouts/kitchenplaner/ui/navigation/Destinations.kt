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

sealed class Destinations {
    companion object {

        const val ShoppingListGraph = "shoppingList_Graph"
        const val Home = "start"
        const val ProjectDetailsGraph = "projectDetails"
        const val ProjectsGraph = "projects"
        const val RecipesGraph = "recipes"
        const val ProjectCreationGraph = "projectCreation_Graph"
        const val RecipeCreationGraph = "recipeCreation_Graph"
        const val RecipeDetailsGraph = "recipeDetails"
        const val ProjectsStart = "projectStart"
        const val ProjectId = "projectID"
    }
}