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

package com.scouts.kitchenplaner.ui.state

import androidx.compose.runtime.mutableStateListOf

/**
 * State object for dietary information of a recipe
 */
class RecipeAllergenState {
    /**
     * List of allergens that are contained in the recipe
     */
    val allergens = mutableStateListOf<String>()

    /**
     * List of allergens of which only traces are contained in the recipe
     */
    val traces = mutableStateListOf<String>()

    /**
     * List of allergens that are not contained in the recipe at all (i.e. not even traces)
     */
    val freeOf = mutableStateListOf<String>()
}