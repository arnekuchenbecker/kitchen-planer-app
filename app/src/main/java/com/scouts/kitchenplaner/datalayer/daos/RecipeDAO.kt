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

package com.scouts.kitchenplaner.datalayer.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.scouts.kitchenplaner.datalayer.dtos.DietarySpecialityIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.IngredientIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.InstructionStepIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.RecipeImageDTO
import com.scouts.kitchenplaner.datalayer.dtos.RecipeStubDTO
import com.scouts.kitchenplaner.datalayer.entities.DietarySpecialityEntity
import com.scouts.kitchenplaner.datalayer.entities.IngredientEntity
import com.scouts.kitchenplaner.datalayer.entities.InstructionEntity
import com.scouts.kitchenplaner.datalayer.entities.RecipeEntity
import com.scouts.kitchenplaner.datalayer.entities.UserRecipeEntity
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

    @Query("UPDATE recipeEntity SET title = :newTitle WHERE id = :id")
    suspend fun updateRecipeName(id: Long, newTitle: String)

    @Query("UPDATE recipeEntity SET description = :newDesc WHERE id = :id")
    suspend fun updateRecipeDescription(id: Long, newDesc: String)

    @Update(RecipeEntity::class)
    suspend fun updateRecipeImage(entity: RecipeImageDTO)

    @Query("UPDATE recipeEntity SET numberOfPeople = :newPeople WHERE id = :id")
    suspend fun updateNumberOfPeople(id: Long, newPeople: Int)

    @Insert
    suspend fun insertInstructionStep(entity: InstructionEntity): Long

    @Delete(InstructionEntity::class)
    suspend fun deleteInstructionStep(entity: InstructionStepIdentifierDTO)

    @Update
    suspend fun updateInstructionStep(entity: InstructionEntity)

    @Query(
        "UPDATE instructionentity " +
                "SET `order` = `order` + 1 " +
                "WHERE recipe = :recipeID " +
                "AND `order` >= :index"
    )
    suspend fun increaseInstructionStepOrder(recipeID: Long, index: Int)

    @Query(
        "UPDATE instructionentity " +
                "SET `order` = `order` - 1 " +
                "WHERE recipe = :recipeID " +
                "AND `order` >= :index"
    )
    suspend fun decreaseInstructionStepOrder(recipeID: Long, index: Int)

    @Insert
    suspend fun insertRecipe(entity: RecipeEntity): Long

    @Insert
    suspend fun insertIngredient(entity: IngredientEntity): Long

    @Delete(IngredientEntity::class)
    suspend fun deleteIngredient(entity: IngredientIdentifierDTO)

    /**
     * Deletes an ingredient group from a recipe by deleting all associated ingredients
     *
     * @param recipeID The ID of the recipe from which to delete the ingredient group
     * @param group The name of the group that should be deleted
     */
    @Query("DELETE FROM ingrediententity WHERE recipe = :recipeID AND ingredientGroup = :group")
    suspend fun deleteIngredientGroup(recipeID: Long, group: String)

    @Query(
        "UPDATE ingrediententity SET name = :newName " +
            "WHERE name = :oldName AND recipe = :recipeID AND ingredientGroup = :group"
    )
    suspend fun updateIngredientName(newName: String, oldName: String, recipeID: Long, group: String)

    @Query(
        "UPDATE ingrediententity SET amount = :newAmount " +
            "WHERE name = :name AND recipe = :recipeID AND ingredientGroup = :group"
    )
    suspend fun updateIngredientAmount(newAmount: Float, name: String, recipeID: Long, group: String)

    @Query(
        "UPDATE ingrediententity SET unit = :newUnit " +
                "WHERE name = :name AND recipe = :recipeID AND ingredientGroup = :group"
    )
    suspend fun updateIngredientUnit(newUnit: String, name: String, recipeID: Long, group: String)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDietarySpeciality(entity: DietarySpecialityEntity): Long

    @Delete(DietarySpecialityEntity::class)
    suspend fun deleteDietarySpeciality(entity: DietarySpecialityIdentifierDTO)

    @Query("SELECT * FROM recipeEntity WHERE id = :id")
    fun getRecipeById(id: Long): Flow<RecipeEntity>

    /**
     * Gets recipe stubs for all recipes with the given IDs
     *
     * @param ids List of ids for which to retrieve recipe stubs
     * @return A list of recipe stubs for the given ids
     */
    @Query(
        "SELECT " +
            "recipeEntity.id AS id, " +
            "recipeEntity.title AS title, " +
            "recipeEntity.imageURI AS imageURI " +
        "FROM recipeEntity " +
        "WHERE id IN (:ids)"
    )
    suspend fun getCurrentRecipeStubsByIDs(ids: List<Long>) : List<RecipeStubDTO>

    @Query("SELECT * FROM ingrediententity WHERE recipe = :id")
    fun getIngredientsByRecipeId(id: Long): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM instructionentity WHERE recipe = :id ORDER BY `order`")
    fun getInstructionsByRecipeId(id: Long): Flow<List<InstructionEntity>>

    @Query(
        "SELECT recipeEntity.id AS id, " +
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
                "WHERE projectId = :projectId"
    )
    fun getRecipesByProjectId(projectId: Long): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM dietaryspecialityentity WHERE recipe = :id")
    fun getAllergensByRecipeId(id: Long): Flow<List<DietarySpecialityEntity>>

    @Query("SELECT * FROM recipeEntity WHERE title LIKE :query")
    fun getRecipesForQueryByName(query: String) : Flow<List<RecipeEntity>>

    @Query("SELECT id FROM recipeEntity WHERE rowId = :rowId")
    suspend fun rowIdToRecipeID(rowId: Long): Long

    @Query("SELECT recipeEntity.id AS id,recipeEntity.title AS title, recipeEntity.imageURI AS imageURI FROM recipeEntity")
    fun getAllRecipeStubs(): Flow<List<RecipeStubDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRecipeUse(entity: UserRecipeEntity): Long

    @Query("SELECT * FROM userRecipe WHERE user = :user ORDER BY lastShown DESC LIMIT :limit")
    fun getLatestRecipesForUser(user: String, limit: Int): Flow<List<UserRecipeEntity>>
}