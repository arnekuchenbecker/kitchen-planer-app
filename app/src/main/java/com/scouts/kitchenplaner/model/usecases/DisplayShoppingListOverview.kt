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

package com.scouts.kitchenplaner.model.usecases

import com.scouts.kitchenplaner.datalayer.repositories.ShoppingListRepository
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.ShoppingList
import com.scouts.kitchenplaner.model.entities.ShoppingListStub
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DisplayShoppingListOverview @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository
) {
    fun getAllShoppingListsForProject(project: Project) : Flow<List<ShoppingList>> {
        return shoppingListRepository.getShoppingListsForProject(project.id)
    }

    fun getShoppingListStubsForProject(project: Project) : Flow<List<ShoppingListStub>> {
        return shoppingListRepository.getShoppingListStubsForProject(project.id)
    }
}