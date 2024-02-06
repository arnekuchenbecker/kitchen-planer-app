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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.scouts.kitchenplaner.datalayer.dtos.ProjectIdDTO
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntity
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDAO {
    @Transaction
    suspend fun createShoppingList(
        shoppingList: ShoppingListEntity,
        entries: List<ShoppingListEntryEntity>
    ) {
        val rowId = insertShoppingList(shoppingList)
        val listId = getShoppingListIdFromRowId(rowId)

        entries.forEach {
            it.listId = listId
        }

        insertShoppingListItems(entries)
    }

    @Insert
    suspend fun insertShoppingList(list: ShoppingListEntity) : Long

    @Insert
    suspend fun insertShoppingListItems(items: List<ShoppingListEntryEntity>)

    @Query("SELECT * FROM shoppingLists WHERE projectId = :projectId")
    fun getShoppingListsByProjectID(projectId: Long) : Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shoppingLists WHERE id = :id")
    fun getShoppingListByID(id: Long) : Flow<ShoppingListEntity>

    @Query("SELECT shoppingListEntries.listId AS listId, " +
            "shoppingListEntries.amount AS amount, " +
            "shoppingListEntries.itemName AS itemName, " +
            "shoppingListEntries.unit AS unit " +
            "FROM shoppingLists " +
            "JOIN shoppingListEntries ON shoppingLists.id = shoppingListEntries.listId " +
            "WHERE shoppingLists.projectId = :projectId")
    fun getShoppingListEntriesByProjectID(projectId: Long) : Flow<List<ShoppingListEntryEntity>>

    @Query("SELECT id FROM shoppingLists WHERE rowId = :rowId")
    suspend fun getShoppingListIdFromRowId(rowId: Long) : Long

    //Methods for archiving Projects
    @Delete(ShoppingListEntity::class)
    suspend fun deleteShoppingListsByProjectId(projectId: ProjectIdDTO)
}