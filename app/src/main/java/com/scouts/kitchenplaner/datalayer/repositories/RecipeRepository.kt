/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming
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

import android.net.Uri
import com.scouts.kitchenplaner.datalayer.daos.RecipeDAO
import com.scouts.kitchenplaner.datalayer.entities.IngredientEntity
import com.scouts.kitchenplaner.datalayer.entities.IngredientGroupEntity
import com.scouts.kitchenplaner.datalayer.entities.InstructionEntity
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.entities.RecipeStub
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeDAO: RecipeDAO
) {
    fun getRecipeStubsByProjectId(id: Long) : Flow<List<RecipeStub>> {
        return recipeDAO.getRecipesByProjectId(id).map {
            it.map { recipe ->
                RecipeStub(recipe.id, recipe.title, Uri.parse(recipe.imageURI))
            }
        }
    }

    suspend fun createRecipe(recipe: Recipe) {

        val ingredientGroups: MutableList<IngredientGroupEntity> =
            mutableListOf()
        val ingredients: MutableList<IngredientEntity> = mutableListOf()
        recipe.ingredientGroups.forEach {
            val entity = it.toDataLayerEntity(recipe.id ?: 0)
            ingredientGroups.add(entity.first)
            ingredients.addAll(entity.second)
        }

        val dataLayerEntity = recipe.toDataLayerEntity()
        recipeDAO.createRecipe(
            recipe = dataLayerEntity.first,
            speciality = dataLayerEntity.second,
            ingredientGroups = ingredientGroups,
            ingredients = ingredients,
            instructions = recipe.instructions.mapIndexed { index, instruction ->
                InstructionEntity(
                    order = index, recipe = 0, instruction = instruction
                )
            })

    }
}