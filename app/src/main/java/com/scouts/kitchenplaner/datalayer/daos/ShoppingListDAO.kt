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
import com.scouts.kitchenplaner.datalayer.dtos.DynamicShoppingListEntryDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectIdDTO
import com.scouts.kitchenplaner.datalayer.dtos.ShoppingListMealSlotIdentifierDTO
import com.scouts.kitchenplaner.datalayer.entities.DynamicShoppingListEntryEntity
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntity
import com.scouts.kitchenplaner.datalayer.entities.StaticShoppingListEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDAO {
    /**
     * Creates the given entities, inserting the listId of the newly created shopping list into all
     * entries before they are created
     *
     * @param shoppingList Entity containing the shopping list's metadata
     * @param dynamicEntries List of all dynamic entries of the shopping list
     * @param staticEntries List of all static entries of the shopping list
     *
     * @return The ID of the newly created shopping list for later reference
     */
    @Transaction
    suspend fun createShoppingList(
        shoppingList: ShoppingListEntity,
        dynamicEntries: List<DynamicShoppingListEntryEntity>,
        staticEntries: List<StaticShoppingListEntryEntity>
    ) : Long {
        val rowId = insertShoppingList(shoppingList)
        val listId = getShoppingListIdFromRowId(rowId)

        dynamicEntries.forEach {
            it.listId = listId
        }

        staticEntries.forEach {
            it.listId = listId
        }

        insertDynamicShoppingListEntries(dynamicEntries)
        insertStaticShoppingListEntries(staticEntries)

        return listId
    }

    @Insert
    suspend fun insertShoppingList(list: ShoppingListEntity) : Long

    /**
     * Create dynamic shopping list entries
     *
     * @param items The entities that should be created in the database
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDynamicShoppingListEntries(items: List<DynamicShoppingListEntryEntity>)

    /**
     * Create static shopping list entries
     *
     * @param items The entities that should be created in the database
     */
    @Insert
    suspend fun insertStaticShoppingListEntries(items: List<StaticShoppingListEntryEntity>)

    @Query("SELECT * FROM shoppingLists WHERE projectId = :projectId")
    fun getShoppingListsByProjectID(projectId: Long) : Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shoppingLists WHERE id = :id")
    fun getShoppingListByID(id: Long) : Flow<ShoppingListEntity>

    /**
     * Retrieve all dynamic entries of a specific shopping list
     *
     * @param listID The ID of the shopping list for which to get the entries
     */
    @Query(
        "SELECT " +
            "recipeEntity.numberOfPeople AS peopleBase, " +
            "ingredientEntity.name AS ingredient, " +
            "ingredientEntity.amount AS amount, " +
            "ingredientEntity.unit AS unit, " +
            "dynamicShoppingListEntries.mealDate AS date, " +
            "dynamicShoppingListEntries.meal AS meal " +
        "FROM dynamicShoppingListEntries " +
        "JOIN recipeProjectMeal " +
            "ON dynamicShoppingListEntries.projectId = recipeProjectMeal.projectId " +
            "AND dynamicShoppingListEntries.meal = recipeProjectMeal.meal " +
            "AND dynamicShoppingListEntries.mealDate = recipeProjectMeal.date " +
        "JOIN recipeEntity " +
            "ON recipeProjectMeal.recipeId = recipeEntity.id " +
        "JOIN ingrediententity " +
            "ON ingrediententity.recipe = recipeEntity.id " +
            "AND ingrediententity.name = dynamicShoppingListEntries.ingredientName " +
        "WHERE dynamicShoppingListEntries.listId = :listID"
    )
    fun getDynamicShoppingListEntriesByListID(listID: Long): Flow<List<DynamicShoppingListEntryDTO>>

    /**
     * Retrieve all static entries of a specific shopping list
     *
     * @param listID The ID of the shopping list for which to get the entries
     */
    @Query("SELECT * FROM staticShoppingListEntries WHERE listId = :listID")
    fun getStaticShoppingListEntriesByListID(listID: Long): Flow<List<StaticShoppingListEntryEntity>>

    @Query("SELECT id FROM shoppingLists WHERE rowId = :rowId")
    suspend fun getShoppingListIdFromRowId(rowId: Long) : Long

    /**
     * Delete all dynamic shopping list entries relevant for a specific meal slot in a given project
     *
     * @param identifier DTO containing the project ID and meal slot the entities that should be
     *                   deleted have to contain
     */
    @Delete(DynamicShoppingListEntryEntity::class)
    suspend fun deleteDynamicEntriesForMealSlot(identifier: ShoppingListMealSlotIdentifierDTO)

    @Delete
    suspend fun deleteShoppingList(entity: ShoppingListEntity)

    //Methods for archiving Projects
    @Delete(ShoppingListEntity::class)
    suspend fun deleteShoppingListsByProjectId(projectId: ProjectIdDTO)
}