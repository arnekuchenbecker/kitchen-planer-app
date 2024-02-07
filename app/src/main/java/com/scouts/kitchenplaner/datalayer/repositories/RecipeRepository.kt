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
import com.scouts.kitchenplaner.datalayer.entities.InstructionEntity
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.datalayer.toModelEntity
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.DietaryTypes
import com.scouts.kitchenplaner.model.entities.Ingredient
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.entities.RecipeStub
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeDAO: RecipeDAO,
) {
    fun getRecipeStubsByProjectId(id: Long): Flow<List<RecipeStub>> {
        return recipeDAO.getRecipesByProjectId(id).map {
            it.map { recipe ->
                RecipeStub(recipe.id, recipe.title, Uri.parse(recipe.imageURI))
            }
        }
    }

    fun getRecipeById(id: Long): Flow<Recipe> {
        val recipeFlow = recipeDAO.getRecipeById(id)
        val ingredientFlow = recipeDAO.getIngredientsByRecipeId(id)
        val instructionsFlow = recipeDAO.getInstructionsByRecipeId(id)
        val dietaryFlow = recipeDAO.getAllergensByRecipeId(id)

        return combine(
            recipeFlow,
            ingredientFlow,
            instructionsFlow,
            dietaryFlow
        ) { recipe, ingredients, instructions, dietaries ->
            val groups = ingredients.groupBy { it.ingredientGroup }.map { (name, ingredients) ->
                IngredientGroup(name, ingredients.map { ingredient ->
                    Ingredient(ingredient.name, ingredient.amount, ingredient.unit)
                })
            }

            val dietaryInformation = dietaries.groupBy { it.type }.map { (type, it) ->
                type to it.map { dietary -> dietary.speciality }
            }.toMap()

            Recipe(
                id = recipe.id,
                name = recipe.title,
                imageURI = Uri.parse(recipe.imageURI),
                description = recipe.description,
                numberOfPeople = recipe.numberOfPeople,
                ingredientGroups = groups,
                instructions = instructions.map { it.instruction },
                traces = dietaryInformation[DietaryTypes.TRACE] ?: listOf(),
                allergens = dietaryInformation[DietaryTypes.ALLERGEN] ?: listOf(),
                freeOfAllergen = dietaryInformation[DietaryTypes.FREE_OF] ?: listOf()
            )
        }
    }

    suspend fun createRecipe(recipe: Recipe) : Long {

        val ingredients: MutableList<IngredientEntity> = mutableListOf()
        recipe.ingredientGroups.forEach {
            val entities = it.toDataLayerEntity(recipe.id)
            ingredients.addAll(entities)
        }

        val dataLayerEntity = recipe.toDataLayerEntity()
        return recipeDAO.createRecipe(
            recipe = dataLayerEntity.first,
            speciality = dataLayerEntity.second,
            ingredients = ingredients,
            instructions = recipe.instructions.mapIndexed { index, instruction ->
                InstructionEntity(
                    order = index, recipe = 0, instruction = instruction
                )
            })

    }

    fun getAllergensForRecipe(id: Long): Flow<List<DietarySpeciality>> {
        return recipeDAO.getAllergensByRecipeId(id).map {
            it.map { entity ->
                entity.toModelEntity()
            }
        }
    }

    fun getAllRecipeStubs(): Flow<List<RecipeStub>> {
        return recipeDAO.getAllRecipeStubs().map {
            it.map { stub ->
                RecipeStub(
                    id = stub.id,
                    name = stub.title,
                    imageURI = Uri.parse(stub.imageURI)
                )
            }
        }
    }
}