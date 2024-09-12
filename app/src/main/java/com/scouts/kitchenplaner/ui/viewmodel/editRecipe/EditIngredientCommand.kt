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
 * Command to edit a ingredient. If one of the values should
 * not be changed they can be left (and set null automatically)
 *
 * @param group The ingredient group where the ingredient to change belongs to
 * @param ingredient The ingredient to change
 * @param newName The new name for the ingredient (or null if the name stays the same)
 * @param newAmount The new amount of the ingredient (or null if it stays the same)
 * @param newUnit The new unit of the ingredient (or null if it stays the same)
 */
class EditIngredientCommand(
    private val group: String,
    private val ingredient: Ingredient,
    private val newName: String? = null,
    private val newAmount: Double? = null,
    private val newUnit: String? = null,
    recipe: Recipe
) : ChangeCommand(recipe = recipe) {
    override fun applyOnState(state: EditRecipeState) {
        val name = newName ?: ingredient.name
        val amount = newAmount ?: ingredient.amount
        val unit = newUnit ?: ingredient.unit
        val newIngredient = Ingredient(name, amount, unit)
        state.alterIngredient(group, ingredient, newIngredient)
    }

    override suspend fun applyOnRecipe(editRecipe: EditRecipe) {
        editRecipe.editIngredient(
            recipe,
            group = IngredientGroup(group, listOf()),
            ingredient = ingredient,
            newName = newName, newAmount = newAmount, newUnit = newUnit
        )
    }
}