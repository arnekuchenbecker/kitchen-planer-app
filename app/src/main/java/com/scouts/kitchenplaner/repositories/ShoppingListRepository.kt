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

package com.scouts.kitchenplaner.repositories

import com.scouts.kitchenplaner.datalayer.daos.ShoppingListDAO
import com.scouts.kitchenplaner.datalayer.dtos.ShoppingListMealSlotIdentifierDTO
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntity
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.shoppinglists.DynamicShoppingListEntry
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingList
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingListEntry
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingListStub
import com.scouts.kitchenplaner.model.entities.shoppinglists.StaticShoppingListEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingListRepository @Inject constructor(
    private val shoppingListDAO: ShoppingListDAO
) {
    /**
     * Create the given Shopping List for the specified project
     *
     * @param list The shopping list to be created
     * @param projectId The ID of the project for which to create the shopping List
     *
     * @return The ID of the newly created shopping list for future reference
     */
    suspend fun createShoppingList(list: ShoppingList, projectId: Long) : Long {
        val entities = list.toDataLayerEntity(projectId)
        return shoppingListDAO.createShoppingList(entities.first, entities.second, entities.third)
    }

    /**
     * Deletes a shopping list
     *
     * @param shoppingList A ShoppingListStub identifying the shopping list that should be deleted
     * @param projectId The ID of the project the shopping list belongs to
     */
    suspend fun deleteShoppingList(shoppingList: ShoppingListStub, projectId: Long) {
        shoppingListDAO.deleteShoppingList(
            ShoppingListEntity(
                shoppingList.id,
                shoppingList.name,
                projectId
            )
        )
    }

    /**
     * Delete all ingredients relevant for the specified MealSlot in all shopping lists of the
     * specified project
     *
     * @param projectID The project from which to delete the entries
     * @param slot The meal slot for which relevant entries should be deleted
     */
    suspend fun deleteEntriesForMealSlot(projectID: Long, slot: MealSlot) {
        shoppingListDAO.deleteDynamicEntriesForMealSlot(
            ShoppingListMealSlotIdentifierDTO(
                projectID,
                slot.meal,
                slot.date
            )
        )
    }

    /**
     * Retrieve the Shopping List with the given ID
     *
     * @param listID The ID of the shopping list to be read from the database
     *
     * @return A flow containing the shopping list with the given ID
     */
    @DomainLayerRestricted
    fun getShoppingList(listID: Long): Flow<ShoppingList> {
        val shoppingListFlow = shoppingListDAO.getShoppingListByID(listID)
        val staticEntriesFlow = shoppingListDAO.getStaticShoppingListEntriesByListID(listID)
        val dynamicEntriesFlow = shoppingListDAO.getDynamicShoppingListEntriesByListID(listID)
        return combine(
            shoppingListFlow,
            staticEntriesFlow,
            dynamicEntriesFlow
        ) { stub, statics, dynamics ->
            val staticEntries: List<ShoppingListEntry> =
                statics.map {
                    StaticShoppingListEntry(
                        it.ingredientName,
                        it.unit,
                        it.amount
                    )
                }
            val dynamicEntries: List<ShoppingListEntry> =
                dynamics.map {
                    DynamicShoppingListEntry(
                        it.ingredient,
                        it.unit,
                        it.amount,
                        it.peopleBase,
                        MealSlot(it.date, it.meal)
                    )
                }
            ShoppingList(
                id = listID,
                name = stub.name,
                items = staticEntries + dynamicEntries
            )
        }
    }

    fun getShoppingListStubsForProject(projectId: Long): Flow<List<ShoppingListStub>> {
        val shoppingLists = shoppingListDAO.getShoppingListsByProjectID(projectId)
        return shoppingLists.map {
            it.map { entity -> ShoppingListStub(entity.id, entity.name) }
        }
    }
}