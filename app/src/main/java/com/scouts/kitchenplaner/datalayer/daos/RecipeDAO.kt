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

package com.scouts.kitchenplaner.datalayer.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.scouts.kitchenplaner.datalayer.dtos.RecipeStubDTO
import com.scouts.kitchenplaner.datalayer.entities.DietarySpecialityEntity
import com.scouts.kitchenplaner.datalayer.entities.IngredientEntity
import com.scouts.kitchenplaner.datalayer.entities.InstructionEntity
import com.scouts.kitchenplaner.datalayer.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDAO {

    @Transaction
    suspend fun createRecipe(
        recipe: RecipeEntity,
        speciality: List<DietarySpecialityEntity>,
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

        instructions.forEach { step ->
            step.recipe = recipeId
            insertInstructionStep(step)
        }
        return recipeId
    }


    @Insert
    suspend fun insertInstructionStep(entity: InstructionEntity): Long

    @Insert
    suspend fun insertRecipe(entity: RecipeEntity): Long

    @Insert
    suspend fun insertIngredient(entity: IngredientEntity): Long

    @Insert
    suspend fun insertDietarySpeciality(entity: DietarySpecialityEntity): Long

    @Query("SELECT * FROM recipeEntity WHERE id = :id")
    fun getRecipeById(id: Long) : Flow<RecipeEntity>

    @Query("SELECT * FROM ingrediententity WHERE recipe = :id")
    fun getIngredientsByRecipeId(id: Long) : Flow<List<IngredientEntity>>

    @Query("SELECT * FROM instructionentity WHERE recipe = :id ORDER BY `order`")
    fun getInstructionsByRecipeId(id: Long) : Flow<List<InstructionEntity>>

    @Query("SELECT recipeEntity.id AS id, " +
            "recipeEntity.title AS title, " +
            "recipeEntity.imageUri AS imageURI, " +
            "recipeEntity.description AS description, " +
            "recipeEntity.numberOfPeople AS numberOfPeople " +
            "FROM recipeEntity JOIN recipeProjectMeal " +
            "WHERE projectId = :projectId " +
            "UNION SELECT recipeEntity.id AS id, " +
            "recipeEntity.title AS title, " +
            "recipeEntity.imageUri AS imageURI, " +
            "recipeEntity.description AS description, " +
            "recipeEntity.numberOfPeople AS numberOfPeople " +
            "FROM recipeEntity JOIN alternativeRecipeProjectMeal " +
            "WHERE projectId = :projectId")
    fun getRecipesByProjectId(projectId: Long) : Flow<List<RecipeEntity>>

    @Query("SELECT * FROM dietaryspecialityentity WHERE recipe = :id")
    fun getAllergensByRecipeId(id: Long) : Flow<List<DietarySpecialityEntity>>

    @Query("SELECT id FROM recipeEntity WHERE rowId = :rowId")
    suspend fun rowIdToRecipeID(rowId: Long): Long

    @Query("SELECT id, title, imageURI FROM recipeEntity")
    suspend fun getAllRecipeStubs(): Flow<List<RecipeStubDTO>>

}