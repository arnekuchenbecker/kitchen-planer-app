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

import com.scouts.kitchenplaner.datalayer.repositories.RecipeRepository
import com.scouts.kitchenplaner.datalayer.repositories.ShoppingListRepository
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.entities.shoppinglists.DynamicShoppingListEntry
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingList
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingListEntry
import com.scouts.kitchenplaner.model.entities.shoppinglists.StaticShoppingListEntry
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for creating new shopping lists
 *
 * @param shoppingListsRepository Repository via which to access the Data Layer
 */
class CreateShoppingList @Inject constructor(
    private val shoppingListsRepository: ShoppingListRepository,
    private val recipeRepository: RecipeRepository
) {
    /**
     * Creates a new shopping list for the given project
     *
     * @param project The project for which to create the shopping list
     * @param list The shopping list that should be created
     *
     * @return The ID of the newly created shopping list
     */
    suspend fun createShoppingList(project: Project, list: ShoppingList) : Long {
        return shoppingListsRepository.createShoppingList(list, project.id)
    }

    /**
     * Creates a Shopping List Entry with the specified name, amount and unit. Note that the created
     * entry is not yet persisted in the data base (create a shopping list via [createShoppingList]
     * to do so)
     */
    @OptIn(DomainLayerRestricted::class)
    fun createShoppingListEntry(name: String, amount: Double, unit: String): ShoppingListEntry {
        return StaticShoppingListEntry(name, unit, amount)
    }

    /**
     * Creates Shopping List Entries for cooking the specified recipe at the specified meal slot.
     * Note that the created entries are not yet persisted in the data base (create a shopping list
     * via [createShoppingList] to do so)
     */
    @OptIn(DomainLayerRestricted::class)
    suspend fun createShoppingListEntriesFromRecipe(
        recipeStub: RecipeStub,
        mealSlot: MealSlot
    ): List<ShoppingListEntry> {
        val recipe = recipeRepository.getRecipeById(recipeStub.id).first()
        val entries = mutableListOf<ShoppingListEntry>()
        recipe.ingredientGroups.forEach { group ->
            entries.addAll(
                group.ingredients.map {
                    DynamicShoppingListEntry(
                        it.name,
                        it.unit,
                        it.amount,
                        recipe.numberOfPeople,
                        mealSlot
                    )
                }
            )
        }
        return entries
    }
}