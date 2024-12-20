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

package com.scouts.kitchenplaner.model.usecases

import com.scouts.kitchenplaner.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.Recipe
import javax.inject.Inject

/**
 * Use case to create a new recipe
 *
 * @param recipeRepository Repository for storing the new recipe
 */
class CreateRecipe @Inject constructor(
    private val recipeRepository: RecipeRepository
) {

    /**
     * Creates a new recipe
     *
     * @param recipe The recipe to be created in the data base
     * @return The id of the newly created recipe
     */
    suspend fun createRecipe(recipe: Recipe): Long {
        return recipeRepository.createRecipe(recipe)
    }
}