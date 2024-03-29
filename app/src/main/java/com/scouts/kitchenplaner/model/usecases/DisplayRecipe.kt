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

import com.scouts.kitchenplaner.datalayer.KitchenAppDataStore
import com.scouts.kitchenplaner.datalayer.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.util.Date
import javax.inject.Inject

class DisplayRecipe @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val userDataStore: KitchenAppDataStore
) {
    fun showRecipeById(id: Long): Flow<Recipe> {
        return recipeRepository.getRecipeById(id).onStart {
            recipeRepository.updateLastShownRecipeForUser(
                userDataStore.getCurrentUser(),
                id,
                Date()
            )
        }
    }
}