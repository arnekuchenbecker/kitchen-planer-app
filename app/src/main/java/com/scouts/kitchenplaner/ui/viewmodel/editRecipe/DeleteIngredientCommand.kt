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

import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.usecases.EditRecipe
import com.scouts.kitchenplaner.ui.state.EditRecipeState

/**
 * Command to delete an ingredient
 *
 * @param group The ingredient group from which the ingredient should be deleted
 * @param ingredient The ingredient to be deleted
 */
class DeleteIngredientCommand(
    private val group: String,
    private val ingredient: Ingredient,
    recipe: Recipe,
    state: EditRecipeState,
    editRecipe: EditRecipe
) :
    ChangeCommand(recipe = recipe, state, editRecipe) {
    override fun applyOnState() {
        state.deleteIngredient(group, ingredient)
    }

    override suspend fun applyOnRecipe() {
        editRecipe.deleteIngredient(
            recipe, ingredient = ingredient, group = IngredientGroup(
                group,
                listOf()
            )
        )
    }
}