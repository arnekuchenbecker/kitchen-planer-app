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
 * Command to update the description of the given recipe
 *
 * @param description The new description for the recipe
 */
class UpdateDescriptionCommand(
    private val description: String, recipe: Recipe, state: EditRecipeState,
    editRecipe: EditRecipe
) :
    ChangeCommand(recipe = recipe, state, editRecipe) {
    override fun applyOnState() {
        state.description = description
    }

    override suspend fun applyOnRecipe() {
        editRecipe.setRecipeDescription(recipe, description)
    }
}