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

package com.scouts.kitchenplaner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingList
import com.scouts.kitchenplaner.model.usecases.CreateShoppingList
import com.scouts.kitchenplaner.ui.state.CreateShoppingListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Viewmodel for creating a shopping list
 *
 * @param createShoppingList Domain Layer access for persisting the shopping list once created.
 */
@HiltViewModel
class CreateShoppingListViewModel @Inject constructor(
    private val createShoppingList: CreateShoppingList
) : ViewModel() {
    /**
     * Object holding the state of the creation of a shopping list
     */
    val state = CreateShoppingListState()

    /**
     * Resets the dynamic shopping list entries in [state]: All existing dynamic entries are removed
     * and then for each recipe given a dynamic entry is added to the list.
     *
     * @param recipes A list of the recipes for which dynamic entries should be added and the meal
     *                slots they are planned for
     */
    fun setDynamicEntries(recipes: List<Pair<MealSlot, RecipeStub>>) {
        state.clearDynamicItems()
        recipes.forEach { (slot, recipe) ->
            viewModelScope.launch {
                val entries = createShoppingList.createShoppingListEntriesFromRecipe(recipe, slot)
                entries.forEach { entry -> state.addDynamicEntry(entry) }
            }
        }
    }

    /**
     * Helper function for adding a static entry to the shopping list
     *
     * @param name The name of the item
     * @param unit The unit of measure
     * @param amount The amount that should be bought
     */
    fun addStaticEntry(name: String, unit: String, amount: Double) {
        state.addStaticEntry(createShoppingList.createShoppingListEntry(name, amount, unit))
    }

    /**
     * Creates and persists the shopping list stored in [state] and associates it with the given
     * project. Only enabled entries will be stored.
     *
     * @param project The project the created shopping list should belong to
     */
    fun createShoppingList(project: Project) {
        val items = state.staticItems.filter { it.enabled }.map { it.item } +
                state.dynamicItems.filter { it.enabled }.map { it.item }

        val shoppingList = ShoppingList(0, state.name, items)

        viewModelScope.launch {
            createShoppingList.createShoppingList(project, shoppingList)
        }
    }
}