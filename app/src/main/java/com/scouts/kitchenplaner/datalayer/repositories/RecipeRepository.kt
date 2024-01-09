/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023  Arne Kuchenbecker, Antonia Heiming
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

package com.scouts.kitchenplaner.datalayer.repositories

import com.scouts.kitchenplaner.datalayer.daos.RecipeDAO
import com.scouts.kitchenplaner.datalayer.entities.DietarySpeciality
import com.scouts.kitchenplaner.datalayer.entities.IngredientEntity
import com.scouts.kitchenplaner.datalayer.entities.IngredientGroupEntity
import com.scouts.kitchenplaner.datalayer.entities.InstructionEntity
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.model.entities.Recipe
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeDAO: RecipeDAO
) {

    suspend fun createRecipe(recipe: Recipe) {

        var ingredientGroup: MutableList<IngredientGroupEntity> =
            mutableListOf()
        var ingredient: MutableList<IngredientEntity> = mutableListOf()
        recipe.ingredients.forEach {
            ingredientGroup.add(
                IngredientGroupEntity(
                    name = it.name, 0
                )
            )
            ingredient.addAll(it.ingredients.map { ing -> IngredientEntity(
                0,
                ingredientGroup = it.name,
                name = ing.name,
                amount = ing.amount,
                unit = ing.unit
            )})
        }

        recipeDAO.createRecipe(
            recipe = recipe.toDataLayerEntity().first,
            speciality = recipe.toDataLayerEntity().second,
            ingredientGroups = ingredientGroup,
            ingredients = ingredient,
            instructions = recipe.instructions.mapIndexed { index, instruction ->
                InstructionEntity(
                    order = index, recipe = 0, instruction = instruction
                )
            })

    }
}