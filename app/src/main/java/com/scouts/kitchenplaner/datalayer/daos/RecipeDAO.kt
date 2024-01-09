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

package com.scouts.kitchenplaner.datalayer.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.scouts.kitchenplaner.datalayer.entities.DietarySpeciality
import com.scouts.kitchenplaner.datalayer.entities.IngredientEntity
import com.scouts.kitchenplaner.datalayer.entities.IngredientGroupEntity
import com.scouts.kitchenplaner.datalayer.entities.InstructionEntity
import com.scouts.kitchenplaner.datalayer.entities.RecipeEntity

@Dao
interface RecipeDAO {

    @Transaction
    suspend fun createRecipe(
        recipe: RecipeEntity,
        speciality: List<DietarySpeciality>,
        ingredientGroups: List<IngredientGroupEntity>,
        ingredients: List<IngredientEntity>,
        instructions: List<InstructionEntity>
    ): Long {
        val rowIdRecipe = insertRecipe(recipe)
        val recipeId = rowIdToRecipeID(rowIdRecipe)

        speciality.forEach { special ->
            special.recipe = recipeId
            insertDietarySpeciality(special)
        }
        ingredients.forEach { ingredient ->
            ingredient.recipe = recipeId
            insertIngredient(ingredient)
        }
        ingredientGroups.forEach { group ->
            group.recipe = recipeId
            insertIngredientGroups(group)
        }

        instructions.forEach { step ->
            step.recipe = recipeId
            insertInstructionStep(step)
        }
        return recipeId
    }


    @Insert
    suspend fun insertInstructionStep(entity: InstructionEntity): Long

    @Insert
    suspend fun insertIngredientGroups(entity: IngredientGroupEntity): Long

    @Insert
    suspend fun insertRecipe(entity: RecipeEntity): Long

    @Insert
    suspend fun insertIngredient(entity: IngredientEntity): Long

    @Insert
    suspend fun insertDietarySpeciality(entity: DietarySpeciality): Long

    @Query("SELECT id FROM RecipeEntity WHERE rowid = :rowId")
    suspend fun rowIdToRecipeID(rowId: Long): Long
}