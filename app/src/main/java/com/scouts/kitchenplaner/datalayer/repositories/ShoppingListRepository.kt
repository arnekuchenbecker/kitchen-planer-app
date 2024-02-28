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

import com.scouts.kitchenplaner.datalayer.daos.ShoppingListDAO
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntity
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.model.entities.ShoppingList
import com.scouts.kitchenplaner.model.entities.ShoppingListItem
import com.scouts.kitchenplaner.model.entities.ShoppingListStub
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingListRepository @Inject constructor(
    private val shoppingListDAO: ShoppingListDAO
) {
    suspend fun createShoppingList(list: ShoppingList, projectId: Long) {
        val entities = list.toDataLayerEntity(projectId)
        shoppingListDAO.createShoppingList(entities.first, entities.second)
    }

    suspend fun deleteShoppingList(shoppingList: ShoppingListStub, projectId: Long) {
        shoppingListDAO.deleteShoppingList(ShoppingListEntity(shoppingList.id, shoppingList.name, projectId))
    }

    fun getShoppingListsForProject(projectId: Long) : Flow<List<ShoppingList>> {
        val shoppingLists = shoppingListDAO.getShoppingListsByProjectID(projectId)
        val shoppingListEntries = shoppingListDAO.getShoppingListEntriesByProjectID(projectId)
        return shoppingLists.combine(shoppingListEntries) { lists, entries ->
            val entryGroups = entries.groupBy { it.listId }
            lists.map { list ->
                ShoppingList(
                    list.id,
                    list.name,
                    entryGroups[list.id]
                        ?.map {
                            ShoppingListItem(it.itemName, it.amount, it.unit)
                        }?.toMutableList() ?: mutableListOf()
                )
            }
        }
    }

    fun getShoppingListStubsForProject(projectId: Long) : Flow<List<ShoppingListStub>> {
        val shoppingLists = shoppingListDAO.getShoppingListsByProjectID(projectId)
        return shoppingLists.map {
            it.map { entity -> ShoppingListStub(entity.id, entity.name) }
        }
    }
}