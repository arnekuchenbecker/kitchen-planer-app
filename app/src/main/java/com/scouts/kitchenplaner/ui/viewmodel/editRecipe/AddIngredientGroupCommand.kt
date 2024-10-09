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

package com.scouts.kitchenplaner.ui.viewmodel.editRecipe

import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.usecases.EditRecipe
import com.scouts.kitchenplaner.ui.state.EditRecipeState

/**
 * Command to add a new ingredient group
 * Note that in a consistent state of a recipe, an ingredient group cannot be empty
 *
 * @param groupName The name of the new ingredient group
 */
class AddIngredientGroupCommand(
    private val groupName: String, recipe: Recipe, state: EditRecipeState,
    editRecipe: EditRecipe
) :
    ChangeCommand(recipe = recipe, state, editRecipe) {
    override fun applyOnState() {
        state.addIngredientGroup(groupName)
    }

    override suspend fun applyOnRecipe() {
        // Nothing to do here
    }
}