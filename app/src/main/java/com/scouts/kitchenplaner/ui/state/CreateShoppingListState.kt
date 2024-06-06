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

package com.scouts.kitchenplaner.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingListEntry

/**
 * Class for holding any state relevant for the creation of a shopping list
 */
class CreateShoppingListState {
    /**
     * The name of the shopping list
     */
    var name by mutableStateOf("")

    /**
     * The static items contained in the shopping list
     */
    val staticItems: List<ShoppingListEntryState>
        get() = _staticItems

    /**
     * The dynamic items contained in the shopping list
     */
    val dynamicItems: List<ShoppingListEntryState>
        get() = _dynamicItems

    private val _staticItems: SnapshotStateList<ShoppingListEntryState> = mutableStateListOf()
    private val _dynamicItems: SnapshotStateList<ShoppingListEntryState> = mutableStateListOf()

    /**
     * Adds a static entry to the shopping list
     *
     * @param entry The entry that should be added
     */
    fun addStaticEntry(entry: ShoppingListEntry) {
        _staticItems.add(ShoppingListEntryState(entry))
    }

    /**
     * Deletes the entry at the specified index in the list
     */
    fun deleteStaticEntry(index: Int) {
        _staticItems.removeAt(index)
    }

    /**
     * Adds a dynamic entry to the shopping list
     *
     * @param entry The entry that should be added
     */
    fun addDynamicEntry(entry: ShoppingListEntry) {
        _dynamicItems.add(ShoppingListEntryState(entry))
    }

    /**
     * Deletes the dynamic entry at the specified index in the list
     */
    fun deleteDynamicEntry(index: Int) {
        _dynamicItems.removeAt(index)
    }

    /**
     * Clears the dynamic list
     */
    fun clearDynamicItems() {
        _dynamicItems.clear()
    }
}