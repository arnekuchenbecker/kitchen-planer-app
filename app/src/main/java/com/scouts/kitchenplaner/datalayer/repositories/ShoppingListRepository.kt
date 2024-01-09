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
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.model.entities.ShoppingList
import javax.inject.Inject

class ShoppingListRepository @Inject constructor(
    private val shoppingListDAO: ShoppingListDAO
) {
    suspend fun createShoppingList(list: ShoppingList, projectId: Long) {
        val entities = list.toDataLayerEntity(projectId)
        shoppingListDAO.createShoppingList(entities.first, entities.second)
    }
}